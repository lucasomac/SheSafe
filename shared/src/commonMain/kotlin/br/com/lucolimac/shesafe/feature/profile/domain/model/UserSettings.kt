package br.com.lucolimac.shesafe.feature.profile.domain.model

/**
 * Enumerates user-configurable boolean preference keys and their persistence metadata.
 *
 * @property storageKey Canonical identifier used for local DataStore and remote Firestore serialization.
 * @property defaultValue Default boolean state applied when the user has not explicitly changed the setting.
 */
enum class SettingKey(
    val storageKey: String,
    val defaultValue: Boolean,
) {
    /** Controls whether a confirmation dialog is displayed before dispatching an emergency SOS alert. */
    SEND_CONFIRMATION(
        storageKey = "SEND_CONFIRMATION",
        defaultValue = true,
    ),
}

/**
 * Immutable snapshot of a user's feature settings keyed by [SettingKey].
 *
 * @property values Map associating each [SettingKey] with its current enabled/disabled state.
 */
data class UserSettings(
    val values: Map<SettingKey, Boolean> = SettingKey.entries.associateWith { it.defaultValue },
) {
    /**
     * Checks whether [key] is enabled, falling back to [SettingKey.defaultValue] when absent from [values].
     *
     * @param key The [SettingKey] to query.
     * @return `true` if the setting is enabled, `false` otherwise.
     */
    fun isEnabled(key: SettingKey): Boolean = values[key] ?: key.defaultValue
}
