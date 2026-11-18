package br.com.lucolimac.shesafe.feature.helprequests.data.repository

import br.com.lucolimac.shesafe.BuildKonfig
import br.com.lucolimac.shesafe.core.domain.model.AppError
import br.com.lucolimac.shesafe.core.domain.model.Resource
import br.com.lucolimac.shesafe.core.logging.logInfo
import br.com.lucolimac.shesafe.core.logging.logWarn
import br.com.lucolimac.shesafe.core.network.safeApiCall
import br.com.lucolimac.shesafe.data.model.InfoBipRequest
import br.com.lucolimac.shesafe.data.model.SmsDevItemRequest
import br.com.lucolimac.shesafe.domain.entity.InfoBipBody
import br.com.lucolimac.shesafe.domain.entity.SmsDevBody
import br.com.lucolimac.shesafe.feature.contacts.domain.model.SecureContact
import br.com.lucolimac.shesafe.feature.helprequests.domain.model.AlertDeliveryOutcome
import br.com.lucolimac.shesafe.feature.helprequests.domain.model.SmsProviderType
import br.com.lucolimac.shesafe.feature.helprequests.domain.repository.EmergencySmsGateway
import br.com.lucolimac.shesafe.framework.service.api.InfoBipService
import br.com.lucolimac.shesafe.framework.service.api.SmsDevService
import br.com.lucolimac.shesafe.platform.SmsRequest
import br.com.lucolimac.shesafe.platform.SmsResult
import br.com.lucolimac.shesafe.platform.SmsService
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.remoteconfig.get
import dev.gitlive.firebase.remoteconfig.remoteConfig
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

/** Firebase Remote Config parameter key selecting the active HTTP SMS provider. */
private const val REMOTE_CONFIG_SMS_SERVER_KEY = "she_safe_api_server"

/** Message type code required by the SMSDev API for standard transactional SMS dispatch. */
private const val SMS_DEV_SEND_TYPE = 9

/** Brazil E.164 country calling prefix prepended to domestic numbers for InfoBip delivery. */
private const val BR_COUNTRY_PREFIX = "55"

/**
 * Abstraction that resolves which remote HTTP [SmsProviderType] should handle outbound emergency alerts.
 */
interface SmsProviderConfigSource {
    /**
     * Fetches and returns the currently active [SmsProviderType].
     *
     * @return The configured [SmsProviderType] for remote SMS dispatch.
     */
    suspend fun getActiveProvider(): SmsProviderType
}

/**
 * [SmsProviderConfigSource] backed by Firebase Remote Config.
 *
 * Defaults to [SmsProviderType.SMS_DEV] whenever Remote Config cannot be fetched or contains an
 * unrecognized value.
 */
class FirebaseSmsProviderConfigSource : SmsProviderConfigSource {
    /**
     * Fetches the latest Firebase Remote Config values and maps `she_safe_api_server` to [SmsProviderType].
     *
     * @return [SmsProviderType.INFO_BIP] when configured explicitly, or [SmsProviderType.SMS_DEV] otherwise.
     */
    override suspend fun getActiveProvider(): SmsProviderType {
        val rawValue = runCatching {
            val remote = Firebase.remoteConfig
            runCatching { remote.fetchAndActivate() }
            remote.get<String?>(REMOTE_CONFIG_SMS_SERVER_KEY).orEmpty()
        }.getOrDefault("")
        return when (rawValue.trim().uppercase()) {
            SmsProviderType.INFO_BIP.name -> SmsProviderType.INFO_BIP
            else -> SmsProviderType.SMS_DEV
        }
    }
}

/**
 * Production implementation of [EmergencySmsGateway] that dispatches SOS messages through the
 * remotely configured HTTP SMS provider and falls back to the platform [SmsService] on failure.
 *
 * @param smsDevService HTTP client for the SMSDev gateway.
 * @param infoBipService HTTP client for the InfoBip gateway.
 * @param nativeSmsService Platform-native SMS sender used as a fallback transport.
 * @param providerConfigSource Source determining which HTTP SMS gateway is active.
 * @param smsDevApiKey API key authenticating requests to SMSDev.
 * @param infoBipApiKey API key authenticating requests to InfoBip.
 */
class EmergencySmsGatewayImpl(
    private val smsDevService: SmsDevService,
    private val infoBipService: InfoBipService,
    private val nativeSmsService: SmsService,
    private val providerConfigSource: SmsProviderConfigSource,
    private val smsDevApiKey: String = BuildKonfig.SMS_DEV_API_KEY,
    private val infoBipApiKey: String = BuildKonfig.INFO_BIP_API_KEY,
) : EmergencySmsGateway {

    /**
     * Sends [message] to all [contacts] with valid digit-only phone numbers.
     *
     * Attempts delivery via the active HTTP provider first; if that fails, falls back to [nativeSmsService],
     * preserving the original HTTP error if native SMS is unsupported or lacks runtime permission.
     *
     * @param contacts Trusted emergency contacts to notify.
     * @param message Fully composed SOS message body.
     * @return A [Resource] containing the [AlertDeliveryOutcome] or an [AppError] describing the failure.
     */
    override suspend fun sendEmergencyAlert(
        contacts: List<SecureContact>,
        message: String,
    ): Resource<AlertDeliveryOutcome> {
        val validPhones = contacts.map { it.phoneNumber.filter(Char::isDigit) }
            .filter { it.isNotBlank() }
        if (validPhones.isEmpty()) {
            return Resource.Error(AppError.Delivery.NoRecipients)
        }

        val activeProvider = providerConfigSource.getActiveProvider()
        logInfo("EmergencySmsGateway", "Attempting emergency alert via $activeProvider")

        val remoteResult = when (activeProvider) {
            SmsProviderType.SMS_DEV -> sendViaSmsDev(validPhones, message)
            SmsProviderType.INFO_BIP -> sendViaInfoBip(validPhones, message)
        }
        if (remoteResult is Resource.Success) {
            return remoteResult
        }

        logWarn("EmergencySmsGateway", "Primary HTTP SMS gateway failed; attempting native SmsService fallback")
        return when (val fallback = sendViaNativeSms(validPhones, message)) {
            is Resource.Success -> fallback
            is Resource.Error -> {
                if (fallback.error is AppError.Delivery.Unsupported ||
                    fallback.error is AppError.Permission.SmsDenied
                ) {
                    remoteResult
                } else {
                    fallback
                }
            }
            is Resource.Loading -> remoteResult
        }
    }

    /**
     * Dispatches [message] to [phones] via the SMSDev HTTP API.
     *
     * @param phones Sanitized digit-only recipient phone numbers.
     * @param message Composed emergency message text.
     * @return A [Resource] wrapping [AlertDeliveryOutcome.SentViaGateway] or an [AppError].
     */
    private suspend fun sendViaSmsDev(
        phones: List<String>,
        message: String,
    ): Resource<AlertDeliveryOutcome> {
        if (smsDevApiKey.isBlank()) {
            return Resource.Error(AppError.Delivery.GatewayRejected("SMS_DEV", "Missing API key"))
        }
        val items = phones.mapNotNull { digits ->
            digits.toLongOrNull()?.let { number ->
                SmsDevItemRequest.fromEntity(
                    SmsDevBody(
                        key = smsDevApiKey,
                        msg = message,
                        number = number,
                        type = SMS_DEV_SEND_TYPE,
                    ),
                )
            }
        }
        if (items.isEmpty()) {
            return Resource.Error(AppError.Validation.InvalidPhoneDigits)
        }
        return when (val apiResult = safeApiCall { smsDevService.sendSms(items) }) {
            is Resource.Success -> {
                val okCount = apiResult.data.count { it.situacao.equals("OK", ignoreCase = true) }
                if (okCount > 0) {
                    Resource.Success(AlertDeliveryOutcome.SentViaGateway(SmsProviderType.SMS_DEV, okCount))
                } else {
                    val reason = apiResult.data.firstOrNull()?.descricao
                    Resource.Error(AppError.Delivery.GatewayRejected("SMS_DEV", reason))
                }
            }
            is Resource.Error -> apiResult
            is Resource.Loading -> Resource.Loading
        }
    }

    /**
     * Dispatches [message] to [phones] via the InfoBip HTTP API, ensuring each destination includes
     * the Brazil country prefix (`55`).
     *
     * @param phones Sanitized digit-only recipient phone numbers.
     * @param message Composed emergency message text.
     * @return A [Resource] wrapping [AlertDeliveryOutcome.SentViaGateway] or an [AppError].
     */
    private suspend fun sendViaInfoBip(
        phones: List<String>,
        message: String,
    ): Resource<AlertDeliveryOutcome> {
        if (infoBipApiKey.isBlank()) {
            return Resource.Error(AppError.Delivery.GatewayRejected("INFO_BIP", "Missing API key"))
        }
        val destinations = phones.map { digits ->
            val withCountry = if (digits.startsWith(BR_COUNTRY_PREFIX)) digits else "$BR_COUNTRY_PREFIX$digits"
            InfoBipBody.Message.Destination(to = withCountry)
        }
        val body = InfoBipBody(
            messages = listOf(
                InfoBipBody.Message(
                    content = InfoBipBody.Message.Content(text = message),
                    destinations = destinations,
                ),
            ),
        )
        val request = InfoBipRequest.fromEntity(body)
        return when (
            val apiResult = safeApiCall {
                infoBipService.sendSms(
                    authorization = "App $infoBipApiKey",
                    request = request,
                )
            }
        ) {
            is Resource.Success -> {
                val sentCount = apiResult.data.messages.size
                if (sentCount > 0) {
                    Resource.Success(AlertDeliveryOutcome.SentViaGateway(SmsProviderType.INFO_BIP, sentCount))
                } else {
                    Resource.Error(AppError.Delivery.GatewayRejected("INFO_BIP", "No accepted destinations"))
                }
            }
            is Resource.Error -> apiResult
            is Resource.Loading -> Resource.Loading
        }
    }

    /**
     * Bridges the callback-based platform [SmsService] into a cancellable suspending call.
     *
     * @param phones Sanitized digit-only recipient phone numbers.
     * @param message Composed emergency message text.
     * @return A [Resource] describing the device-level SMS outcome or failure.
     */
    private suspend fun sendViaNativeSms(
        phones: List<String>,
        message: String,
    ): Resource<AlertDeliveryOutcome> = suspendCancellableCoroutine { cont ->
        nativeSmsService.send(
            SmsRequest(recipients = phones, message = message),
        ) { smsResult ->
            if (!cont.isActive) return@send
            val mapped: Resource<AlertDeliveryOutcome> = when (smsResult) {
                SmsResult.Sent -> Resource.Success(AlertDeliveryOutcome.SentViaDeviceSms)
                SmsResult.UserConfirmationRequired -> Resource.Success(AlertDeliveryOutcome.UserConfirmationRequired)
                SmsResult.Unsupported -> Resource.Error(AppError.Delivery.Unsupported)
                is SmsResult.Failed -> Resource.Error(AppError.Delivery.GatewayRejected("NATIVE_SMS", smsResult.reason))
            }
            cont.resume(mapped)
        }
    }
}
