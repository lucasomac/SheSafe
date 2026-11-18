package br.com.lucolimac.shesafe.core.domain.model

sealed class AppError {
    sealed class Network : AppError() {
        data object NoConnection : Network()
        data object Timeout : Network()
        data object Unauthorized : Network()
        data class RateLimited(val retryAfterSeconds: Long) : Network()
        data class ServerError(val code: Int, val details: String? = null) : Network()
    }

    sealed class Database : AppError() {
        data object NotFound : Database()
        data class ReadFailure(val causeMessage: String? = null) : Database()
        data class WriteFailure(val causeMessage: String? = null) : Database()
    }

    sealed class Auth : AppError() {
        data object Unauthenticated : Auth()
        data class SignInFailed(val reason: String? = null) : Auth()
        data class SignOutFailed(val reason: String? = null) : Auth()
    }

    sealed class Permission : AppError() {
        data object LocationDenied : Permission()
        data object SmsDenied : Permission()
    }

    sealed class Delivery : AppError() {
        data object Unsupported : Delivery()
        data object NoRecipients : Delivery()
        data class GatewayRejected(val provider: String, val reason: String? = null) : Delivery()
    }

    sealed class Validation : AppError() {
        data object EmptyName : Validation()
        data object EmptyPhone : Validation()
        data object InvalidPhoneDigits : Validation()
        data object EmptyMessage : Validation()
    }

    data class Unexpected(val causeMessage: String? = null) : AppError()
}

val AppError.isRecoverable: Boolean
    get() = when (this) {
        is AppError.Network.NoConnection,
        is AppError.Network.Timeout,
        is AppError.Network.RateLimited,
        is AppError.Database.WriteFailure,
        is AppError.Delivery.GatewayRejected,
        -> true
        is AppError.Network.ServerError -> code in 500..599
        else -> false
    }
