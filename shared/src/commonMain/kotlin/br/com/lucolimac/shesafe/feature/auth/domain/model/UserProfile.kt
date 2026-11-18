package br.com.lucolimac.shesafe.feature.auth.domain.model

/**
 * Represents the authenticated user's profile information in the domain layer.
 *
 * @property uid Unique identifier assigned to the user by the authentication provider.
 * @property email Primary email address associated with the user account, or `null` if unavailable.
 * @property displayName Human-readable display name of the user, or `null` if not set.
 * @property photoUrl URL pointing to the user's profile avatar image, or `null` if unavailable.
 */
data class UserProfile(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
)
