package br.com.lucolimac.shesafe.feature.auth.domain.model

data class UserProfile(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
)
