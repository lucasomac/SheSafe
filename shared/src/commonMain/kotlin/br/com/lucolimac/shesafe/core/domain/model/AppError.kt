package br.com.lucolimac.shesafe.core.domain.model

/**
 * Canonical domain error hierarchy representing all categorized failure modes in SheSafe.
 */
sealed class AppError {
    /**
     * Errors originating from HTTP transport, connectivity, or remote API responses.
     */
    sealed class Network : AppError() {
        /**
         * Indicates that the device has no active network connectivity or DNS/socket resolution failed.
         */
        data object NoConnection : Network()

        /**
         * Indicates that a network connect, socket, or request timeout expired before completion.
         */
        data object Timeout : Network()

        /**
         * Indicates that the remote server rejected credentials with HTTP 401 Unauthorized.
         */
        data object Unauthorized : Network()

        /**
         * Indicates that the client exceeded remote API rate limits (HTTP 429 Too Many Requests).
         *
         * @property retryAfterSeconds Suggested delay in seconds before retrying the request.
         */
        data class RateLimited(val retryAfterSeconds: Long) : Network()

        /**
         * Indicates an HTTP error status returned by the remote server.
         *
         * @property code HTTP status code returned in the response.
         * @property details Optional human-readable status description or error payload summary.
         */
        data class ServerError(val code: Int, val details: String? = null) : Network()
    }

    /**
     * Errors originating from local Room or remote Firestore persistence operations.
     */
    sealed class Database : AppError() {
        /**
         * Indicates that the requested record or resource does not exist.
         */
        data object NotFound : Database()

        /**
         * Indicates a failure while reading or deserializing persisted data.
         *
         * @property causeMessage Optional diagnostic message from the underlying storage exception.
         */
        data class ReadFailure(val causeMessage: String? = null) : Database()

        /**
         * Indicates a failure while inserting, updating, or deleting persisted data.
         *
         * @property causeMessage Optional diagnostic message from the underlying storage exception.
         */
        data class WriteFailure(val causeMessage: String? = null) : Database()
    }

    /**
     * Errors related to user authentication and session lifecycle.
     */
    sealed class Auth : AppError() {
        /**
         * Indicates that an operation requires an authenticated user, but no active session exists.
         */
        data object Unauthenticated : Auth()

        /**
         * Indicates that a sign-in attempt failed or was cancelled.
         *
         * @property reason Optional diagnostic description of why sign-in failed.
         */
        data class SignInFailed(val reason: String? = null) : Auth()

        /**
         * Indicates that signing out of the current session failed.
         *
         * @property reason Optional diagnostic description of why sign-out failed.
         */
        data class SignOutFailed(val reason: String? = null) : Auth()
    }

    /**
     * Errors caused by missing or denied runtime OS permissions.
     */
    sealed class Permission : AppError() {
        /**
         * Indicates that location access permission was denied by the user or OS.
         */
        data object LocationDenied : Permission()

        /**
         * Indicates that SMS sending permission was denied by the user or OS.
         */
        data object SmsDenied : Permission()
    }

    /**
     * Errors occurring while dispatching emergency SMS or gateway notifications.
     */
    sealed class Delivery : AppError() {
        /**
         * Indicates that direct SMS/alert delivery is unsupported on the current device or platform.
         */
        data object Unsupported : Delivery()

        /**
         * Indicates that an alert could not be sent because no recipient phone numbers were provided.
         */
        data object NoRecipients : Delivery()

        /**
         * Indicates that an external messaging gateway rejected the dispatch request.
         *
         * @property provider Identifier or name of the SMS gateway provider that rejected the message.
         * @property reason Optional error description returned by the gateway provider.
         */
        data class GatewayRejected(val provider: String, val reason: String? = null) : Delivery()
    }

    /**
     * Client-side input validation errors detected before executing domain operations.
     */
    sealed class Validation : AppError() {
        /**
         * Indicates that a required contact or user name field is blank.
         */
        data object EmptyName : Validation()

        /**
         * Indicates that a required phone number field is blank.
         */
        data object EmptyPhone : Validation()

        /**
         * Indicates that a phone number does not contain the expected number of valid digits.
         */
        data object InvalidPhoneDigits : Validation()

        /**
         * Indicates that an emergency or custom SMS message body is blank.
         */
        data object EmptyMessage : Validation()
    }

    /**
     * Fallback error representing an unclassified exception not covered by specific domain subtypes.
     *
     * @property causeMessage Optional message from the unexpected throwable.
     */
    data class Unexpected(val causeMessage: String? = null) : AppError()
}

/**
 * Whether this [AppError] represents a transient condition that may succeed if retried later.
 */
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
