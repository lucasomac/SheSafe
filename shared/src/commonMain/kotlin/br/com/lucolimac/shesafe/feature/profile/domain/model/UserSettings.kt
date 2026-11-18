package br.com.lucolimac.shesafe.feature.profile.domain.model

enum class SettingKey(
    val storageKey: String,
    val defaultValue: Boolean,
) {
    SEND_CONFIRMATION(
        storageKey = "SEND_CONFIRMATION",
        defaultValue = true,
    ),
}

data class UserSettings(
    val values: Map<SettingKey, Boolean> = SettingKey.entries.associateWith { it.defaultValue },
) {
    fun isEnabled(key: SettingKey): Boolean = values[key] ?: key.defaultValue
}
