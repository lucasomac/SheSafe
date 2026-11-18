package br.com.lucolimac.shesafe.core.network

import br.com.lucolimac.shesafe.core.domain.model.AppError
import br.com.lucolimac.shesafe.core.domain.model.Resource
import br.com.lucolimac.shesafe.core.logging.logWarn
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.io.IOException
import kotlin.time.Duration.Companion.milliseconds

/**
 * Fallback cooldown duration in seconds used when an HTTP 429 response omits a numeric `Retry-After` header.
 */
private const val DEFAULT_RETRY_AFTER_SECONDS = 60L

/**
 * Maximum `Retry-After` window in seconds eligible for transparent single automatic retry inside [safeApiCall].
 */
private const val MAX_AUTO_RETRY_AFTER_SECONDS = 10L

/**
 * Conversion factor from whole seconds to milliseconds.
 */
private const val MILLIS_PER_SECOND = 1000L

/**
 * Executes a suspending network [call] and maps transport or HTTP failures into a typed [Resource].
 *
 * Rethrows [CancellationException] to preserve structured concurrency. When an HTTP 429 response is
 * received with a short `Retry-After` header (`<= 10` seconds) and [allowRateLimitRetry] is `true`,
 * suspends for the requested interval and performs at most one automatic retry.
 *
 * @param T Expected return payload type of [call].
 * @param allowRateLimitRetry Whether a short HTTP 429 `Retry-After` delay may be automatically retried once.
 * @param call Suspending lambda performing the Ktor/Ktorfit network request.
 * @return [Resource.Success] containing the response value, or [Resource.Error] with a mapped [AppError].
 */
suspend fun <T> safeApiCall(
    allowRateLimitRetry: Boolean = true,
    call: suspend () -> T,
): Resource<T> {
    return try {
        Resource.Success(call())
    } catch (e: CancellationException) {
        throw e
    } catch (e: HttpRequestTimeoutException) {
        logWarn("SafeApiCall", "HTTP timeout: ${e.message}")
        Resource.Error(AppError.Network.Timeout)
    } catch (e: ClientRequestException) {
        when (val status = e.response.status) {
            HttpStatusCode.Unauthorized -> Resource.Error(AppError.Network.Unauthorized)
            HttpStatusCode.TooManyRequests -> {
                val retryAfter = e.response.headers["Retry-After"]?.toLongOrNull()
                    ?: DEFAULT_RETRY_AFTER_SECONDS
                if (allowRateLimitRetry && retryAfter <= MAX_AUTO_RETRY_AFTER_SECONDS) {
                    delay((retryAfter * MILLIS_PER_SECOND).milliseconds)
                    return safeApiCall(allowRateLimitRetry = false, call = call)
                }
                Resource.Error(AppError.Network.RateLimited(retryAfter))
            }
            HttpStatusCode.NotFound -> Resource.Error(AppError.Database.NotFound)
            else -> Resource.Error(
                AppError.Network.ServerError(
                    code = status.value,
                    details = status.description,
                ),
            )
        }
    } catch (e: ServerResponseException) {
        Resource.Error(
            AppError.Network.ServerError(
                code = e.response.status.value,
                details = e.response.status.description,
            ),
        )
    } catch (e: IOException) {
        logWarn("SafeApiCall", "Network IO error: ${e.message}")
        Resource.Error(AppError.Network.NoConnection)
    } catch (e: Exception) {
        logWarn("SafeApiCall", "Unexpected network failure: ${e.message}")
        Resource.Error(AppError.Unexpected(e.message))
    }
}
