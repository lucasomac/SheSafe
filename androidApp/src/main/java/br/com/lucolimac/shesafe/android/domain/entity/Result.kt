package br.com.lucolimac.shesafe.android.domain.entity

internal sealed class Result<out S, out E> {
    data class Success<S>(
        val data: S
    ) : Result<S, Nothing>()

    data class Failure<E>(
        val data: E
    ) : Result<Nothing, E>()
}
