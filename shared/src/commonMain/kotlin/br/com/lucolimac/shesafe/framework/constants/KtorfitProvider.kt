package br.com.lucolimac.shesafe.framework.constants

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object KtorfitProvider {
    fun provideHttpClient(): HttpClient {
        return HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    coerceInputValues = true
                })
            }
        }
    }

    fun provideKtorfit(httpClient: HttpClient): Ktorfit {
        return Ktorfit.Builder()
            .httpClient(httpClient)
            .baseUrl("https://api.smsdev.com.br/") // default baseUrl, or configure per service if needed
            .build()
    }
}
