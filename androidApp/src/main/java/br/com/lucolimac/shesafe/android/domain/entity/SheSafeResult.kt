package br.com.lucolimac.shesafe.android.domain.entity

sealed class SheSafeResult<out S, out E> {
    data class Success<S>(
        val data: S
    ) : SheSafeResult<S, Nothing>()

    data class Failure<E>(
        val data: E
    ) : SheSafeResult<Nothing, E>()

    data class Error(
        val throwable: Throwable? = null
    ) : SheSafeResult<Nothing, Nothing>()
}
