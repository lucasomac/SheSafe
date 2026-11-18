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

const val QUALIFIER_SMS_DEV_KTORFIT = "smsDevKtorfit"
const val QUALIFIER_INFO_BIP_KTORFIT = "infoBipKtorfit"

private const val REQUEST_TIMEOUT_MS = 30_000L
private const val CONNECT_TIMEOUT_MS = 15_000L
private const val SOCKET_TIMEOUT_MS = 30_000L
private const val MAX_RETRY_DELAY_MS = 10_000L
private const val JITTER_MS = 500L
private const val HTTP_SERVER_ERROR_MIN = 500
private const val HTTP_SERVER_ERROR_MAX = 599

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
