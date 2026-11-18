package br.com.lucolimac.shesafe.core.network

import br.com.lucolimac.shesafe.BuildKonfig
import br.com.lucolimac.shesafe.core.logging.logDebug
import br.com.lucolimac.shesafe.framework.service.api.InfoBipService
import br.com.lucolimac.shesafe.framework.service.api.SmsDevService
import br.com.lucolimac.shesafe.framework.service.api.createInfoBipService
import br.com.lucolimac.shesafe.framework.service.api.createSmsDevService
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.accept
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Koin qualifier name identifying the [Ktorfit] instance configured for the SMSDev gateway API.
 */
const val QUALIFIER_SMS_DEV_KTORFIT = "smsDevKtorfit"

/**
 * Koin qualifier name identifying the [Ktorfit] instance configured for the Infobip SMS gateway API.
 */
const val QUALIFIER_INFO_BIP_KTORFIT = "infoBipKtorfit"

/**
 * Maximum duration in milliseconds allowed for an entire HTTP request-response cycle.
 */
private const val REQUEST_TIMEOUT_MS = 30_000L

/**
 * Maximum duration in milliseconds allowed when establishing a TCP/TLS connection.
 */
private const val CONNECT_TIMEOUT_MS = 15_000L

/**
 * Maximum inactivity duration in milliseconds between two data packets on an open socket.
 */
private const val SOCKET_TIMEOUT_MS = 30_000L

/**
 * Upper bound in milliseconds for exponential backoff delays between HTTP retries.
 */
private const val MAX_RETRY_DELAY_MS = 10_000L

/**
 * Randomization jitter window in milliseconds added to exponential retry delays.
 */
private const val JITTER_MS = 500L

/**
 * Lowest HTTP status code (inclusive) classified as a 5xx server error for retry policies.
 */
private const val HTTP_SERVER_ERROR_MIN = 500

/**
 * Highest HTTP status code (inclusive) classified as a 5xx server error for retry policies.
 */
private const val HTTP_SERVER_ERROR_MAX = 599

/**
 * Builds and configures the shared multiplatform Ktor [HttpClient].
 *
 * Installs JSON content negotiation, request/connect/socket timeouts, exponential-backoff retries
 * for transient 5xx responses and network exceptions, and header-sanitized debug logging.
 *
 * @return A configured [HttpClient] ready for use by Ktorfit API services.
 */
@OptIn(ExperimentalSerializationApi::class)
fun createAppHttpClient(): HttpClient = HttpClient {
    expectSuccess = true

    defaultRequest {
        contentType(ContentType.Application.Json)
        accept(ContentType.Application.Json)
    }

    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                isLenient = true
                coerceInputValues = true
                explicitNulls = false
            },
        )
    }

    install(HttpTimeout) {
        requestTimeoutMillis = REQUEST_TIMEOUT_MS
        connectTimeoutMillis = CONNECT_TIMEOUT_MS
        socketTimeoutMillis = SOCKET_TIMEOUT_MS
    }

    install(HttpRequestRetry) {
        retryIf(maxRetries = 3) { _, response ->
            val code = response.status.value
            code in HTTP_SERVER_ERROR_MIN..HTTP_SERVER_ERROR_MAX &&
                response.status != HttpStatusCode.TooManyRequests
        }
        retryOnException(maxRetries = 2, retryOnTimeout = true)
        exponentialDelay(base = 2.0, maxDelayMs = MAX_RETRY_DELAY_MS, randomizationMs = JITTER_MS)
    }

    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                logDebug("KtorHttp", message)
            }
        }
        sanitizeHeader { header -> header == HttpHeaders.Authorization }
        level = if (BuildKonfig.IS_DEBUG) LogLevel.HEADERS else LogLevel.NONE
    }
}

/**
 * Koin module providing the shared [HttpClient], qualified [Ktorfit] instances,
 * and generated [SmsDevService] and [InfoBipService] API clients.
 */
val networkModule = module {
    single<HttpClient> { createAppHttpClient() }

    single<Ktorfit>(named(QUALIFIER_SMS_DEV_KTORFIT)) {
        Ktorfit.Builder()
            .httpClient(get<HttpClient>())
            .baseUrl(BuildKonfig.SMS_DEV_HOST)
            .build()
    }

    single<Ktorfit>(named(QUALIFIER_INFO_BIP_KTORFIT)) {
        Ktorfit.Builder()
            .httpClient(get<HttpClient>())
            .baseUrl(BuildKonfig.INFO_BIP_HOST)
            .build()
    }

    single<SmsDevService> {
        get<Ktorfit>(named(QUALIFIER_SMS_DEV_KTORFIT)).createSmsDevService()
    }

    single<InfoBipService> {
        get<Ktorfit>(named(QUALIFIER_INFO_BIP_KTORFIT)).createInfoBipService()
    }
}
