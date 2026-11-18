package br.com.lucolimac.shesafe.feature.profile.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import br.com.lucolimac.shesafe.core.domain.model.AppError
import br.com.lucolimac.shesafe.core.domain.model.Resource
import br.com.lucolimac.shesafe.core.logging.logWarn
import br.com.lucolimac.shesafe.feature.auth.domain.repository.AuthRepository
import br.com.lucolimac.shesafe.feature.profile.domain.model.SettingKey
import br.com.lucolimac.shesafe.feature.profile.domain.model.UserSettings
import br.com.lucolimac.shesafe.feature.profile.domain.repository.ProfilePreferencesRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

private const val COLLECTION_PERSONAL_DATA = "personalData"
private const val COLLECTION_SETTINGS = "settings"

@Serializable
private data class PersonalMessageDto(
    val message: String = "",
)

@Serializable
private data class SettingsFirestoreDto(
    @SerialName("SEND_CONFIRMATION")
    val sendConfirmation: Boolean? = null,
)

class ProfilePreferencesRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
    private val authRepository: AuthRepository,
) : ProfilePreferencesRepository {

    private fun messageKey(email: String) = stringPreferencesKey("help_message_$email")

    private fun settingPrefKey(email: String, key: SettingKey) =
        booleanPreferencesKey("setting_${email}_${key.storageKey}")

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeHelpMessage(): Flow<String> =
        authRepository.observeCurrentUser().flatMapLatest { user ->
            val email = user?.email
            if (email.isNullOrBlank()) {
                flowOf("")
            } else {
                dataStore.data.map { prefs -> prefs[messageKey(email)].orEmpty() }
            }
        }

    override suspend fun refreshHelpMessage(): Resource<String> {
        val email = authRepository.getCurrentUser()?.email
            ?: return Resource.Error(AppError.Auth.Unauthenticated)
        return try {
            val doc = Firebase.firestore
                .collection(COLLECTION_PERSONAL_DATA)
                .document(email)
                .get()
            val remoteMessage = if (doc.exists) {
                runCatching { doc.data(PersonalMessageDto.serializer()).message }.getOrDefault("")
            } else {
                ""
            }
            dataStore.edit { prefs ->
                prefs[messageKey(email)] = remoteMessage
            }
            Resource.Success(remoteMessage)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            logWarn("ProfilePrefsRepo", "refreshHelpMessage failed, using DataStore cache: ${e.message}")
            Resource.Error(AppError.Network.NoConnection)
        }
    }

    override suspend fun updateHelpMessage(message: String): Resource<Unit> {
        val email = authRepository.getCurrentUser()?.email
            ?: return Resource.Error(AppError.Auth.Unauthenticated)
        return try {
            dataStore.edit { prefs ->
                prefs[messageKey(email)] = message
            }
            runCatching {
                Firebase.firestore
                    .collection(COLLECTION_PERSONAL_DATA)
                    .document(email)
                    .set(
                        strategy = PersonalMessageDto.serializer(),
                        data = PersonalMessageDto(message = message),
                        merge = true,
                    )
            }.onFailure {
                logWarn("ProfilePrefsRepo", "Remote help message sync deferred: ${it.message}")
            }
            Resource.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(AppError.Database.WriteFailure(e.message))
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeSettings(): Flow<UserSettings> =
        authRepository.observeCurrentUser().flatMapLatest { user ->
            val email = user?.email
            if (email.isNullOrBlank()) {
                flowOf(UserSettings())
            } else {
                dataStore.data.map { prefs ->
                    val map = SettingKey.entries.associateWith { key ->
                        prefs[settingPrefKey(email, key)] ?: key.defaultValue
                    }
                    UserSettings(values = map)
                }
            }
        }

    override suspend fun refreshSettings(): Resource<UserSettings> {
        val email = authRepository.getCurrentUser()?.email
            ?: return Resource.Error(AppError.Auth.Unauthenticated)
        return try {
            val doc = Firebase.firestore
                .collection(COLLECTION_SETTINGS)
                .document(email)
                .get()
            val dto = if (doc.exists) {
                runCatching { doc.data(SettingsFirestoreDto.serializer()) }.getOrNull()
            } else {
                null
            }
            val sendConfirmation = dto?.sendConfirmation ?: SettingKey.SEND_CONFIRMATION.defaultValue
            dataStore.edit { prefs ->
                prefs[settingPrefKey(email, SettingKey.SEND_CONFIRMATION)] = sendConfirmation
            }
            Resource.Success(
                UserSettings(
                    values = mapOf(SettingKey.SEND_CONFIRMATION to sendConfirmation),
                ),
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            logWarn("ProfilePrefsRepo", "refreshSettings failed, using DataStore cache: ${e.message}")
            Resource.Error(AppError.Network.NoConnection)
        }
    }

    override suspend fun updateSetting(key: SettingKey, enabled: Boolean): Resource<Unit> {
        val email = authRepository.getCurrentUser()?.email
            ?: return Resource.Error(AppError.Auth.Unauthenticated)
        return try {
            dataStore.edit { prefs ->
                prefs[settingPrefKey(email, key)] = enabled
            }
            runCatching {
                val payload = when (key) {
                    SettingKey.SEND_CONFIRMATION -> SettingsFirestoreDto(sendConfirmation = enabled)
                }
                Firebase.firestore
                    .collection(COLLECTION_SETTINGS)
                    .document(email)
                    .set(
                        strategy = SettingsFirestoreDto.serializer(),
                        data = payload,
                        merge = true,
                    )
            }.onFailure {
                logWarn("ProfilePrefsRepo", "Remote setting sync deferred: ${it.message}")
            }
            Resource.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(AppError.Database.WriteFailure(e.message))
        }
    }
}
