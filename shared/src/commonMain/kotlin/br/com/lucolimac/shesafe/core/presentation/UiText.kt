package br.com.lucolimac.shesafe.core.presentation

import br.com.lucolimac.shesafe.core.domain.model.AppError
import org.jetbrains.compose.resources.StringResource
import shesafe.shared.generated.resources.Res
import shesafe.shared.generated.resources.error_auth_failed
import shesafe.shared.generated.resources.error_generic
import shesafe.shared.generated.resources.error_message_blank
import shesafe.shared.generated.resources.error_name_blank
import shesafe.shared.generated.resources.error_no_connection
import shesafe.shared.generated.resources.error_not_found
import shesafe.shared.generated.resources.error_phone_blank
import shesafe.shared.generated.resources.error_phone_less_eleven_digits
import shesafe.shared.generated.resources.error_rate_limited
import shesafe.shared.generated.resources.error_timeout
import shesafe.shared.generated.resources.error_unauthorized
import shesafe.shared.generated.resources.sms_not_sent

fun AppError.toStringResource(): StringResource = when (this) {
    AppError.Network.NoConnection -> Res.string.error_no_connection
    AppError.Network.Timeout -> Res.string.error_timeout
    AppError.Network.Unauthorized,
    AppError.Auth.Unauthenticated,
    -> Res.string.error_unauthorized
    is AppError.Network.RateLimited -> Res.string.error_rate_limited
    is AppError.Network.ServerError -> Res.string.error_generic
    AppError.Database.NotFound -> Res.string.error_not_found
    is AppError.Database.ReadFailure,
    is AppError.Database.WriteFailure,
    -> Res.string.error_generic
    is AppError.Auth.SignInFailed,
    is AppError.Auth.SignOutFailed,
    -> Res.string.error_auth_failed
    AppError.Permission.LocationDenied,
    AppError.Permission.SmsDenied,
    -> Res.string.sms_not_sent
    AppError.Delivery.Unsupported,
    AppError.Delivery.NoRecipients,
    is AppError.Delivery.GatewayRejected,
    -> Res.string.sms_not_sent
    AppError.Validation.EmptyName -> Res.string.error_name_blank
    AppError.Validation.EmptyPhone -> Res.string.error_phone_blank
    AppError.Validation.InvalidPhoneDigits -> Res.string.error_phone_less_eleven_digits
    AppError.Validation.EmptyMessage -> Res.string.error_message_blank
    is AppError.Unexpected -> Res.string.error_generic
}
