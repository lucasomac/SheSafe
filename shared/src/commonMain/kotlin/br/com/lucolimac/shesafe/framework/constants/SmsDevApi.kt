package br.com.lucolimac.shesafe.framework.constants

import br.com.lucolimac.shesafe.framework.constants.Endpoints.INFO_BIP_HOST
import br.com.lucolimac.shesafe.framework.constants.Endpoints.SMS_DEV_HOST
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object SmsDevApi {
    fun provideHttpClient(): HttpClient {
        return HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    coerceInputValues = true
                })
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        println("KTOR_LOG: $message")
                    }
                }
                level = LogLevel.BODY
            }
        }
    }

    fun provideKtorfitSmsDev(httpClient: HttpClient): Ktorfit {
        return Ktorfit.Builder().httpClient(httpClient).baseUrl(SMS_DEV_HOST).build()
    }

    fun provideKtorfitInfoBip(httpClient: HttpClient): Ktorfit {
        return Ktorfit.Builder().httpClient(httpClient).baseUrl(INFO_BIP_HOST).build()
    }
}
