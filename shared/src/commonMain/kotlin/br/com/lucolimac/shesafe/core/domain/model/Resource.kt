package br.com.lucolimac.shesafe.core.domain.model

/**
 * Discriminated union representing the state and outcome of a domain or data operation.
 *
 * @param T Covariant payload type carried when the operation succeeds.
 */
sealed class Resource<out T> {
    /**
     * Represents a completed operation yielding [data].
     *
     * @param T Type of the successful result payload.
     * @property data Resulting value produced by the operation.
     */
    data class Success<out T>(val data: T) : Resource<T>()

    /**
     * Represents a failed operation carrying a typed domain [AppError].
     *
     * @property error Categorized [AppError] describing the failure.
     */
    data class Error(val error: AppError) : Resource<Nothing>()

    /**
     * Represents an in-flight operation whose final result is not yet available.
     */
    data object Loading : Resource<Nothing>()
}

/**
 * Invokes [action] with the encapsulated payload if this [Resource] is [Resource.Success],
 * returning the original [Resource] unchanged for call chaining.
 *
 * @param T Payload type of the [Resource].
 * @param action Callback executed with [Resource.Success.data] when successful.
 * @return This [Resource] instance unchanged.
 */
inline fun <T> Resource<T>.onSuccess(action: (T) -> Unit): Resource<T> {
    if (this is Resource.Success) action(data)
    return this
}

/**
 * Invokes [action] with the encapsulated [AppError] if this [Resource] is [Resource.Error],
 * returning the original [Resource] unchanged for call chaining.
 *
 * @param T Payload type of the [Resource].
 * @param action Callback executed with [Resource.Error.error] on failure.
 * @return This [Resource] instance unchanged.
 */
inline fun <T> Resource<T>.onError(action: (AppError) -> Unit): Resource<T> {
    if (this is Resource.Error) action(error)
    return this
}

/**
 * Transforms the payload of a [Resource.Success] using [transform] while preserving
 * [Resource.Error] and [Resource.Loading] states unchanged.
 *
 * @param T Source payload type.
 * @param R Transformed payload type.
 * @param transform Mapping function applied to [Resource.Success.data].
 * @return A new [Resource] containing the transformed value or the original non-success state.
 */
inline fun <T, R> Resource<T>.map(transform: (T) -> R): Resource<R> = when (this) {
    is Resource.Success -> Resource.Success(transform(data))
    is Resource.Error -> this
    is Resource.Loading -> Resource.Loading
}
