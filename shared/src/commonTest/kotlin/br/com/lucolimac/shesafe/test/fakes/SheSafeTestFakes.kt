package br.com.lucolimac.shesafe.test.fakes

import br.com.lucolimac.shesafe.core.domain.model.AppError
import br.com.lucolimac.shesafe.core.domain.model.Resource
import br.com.lucolimac.shesafe.feature.auth.domain.model.UserProfile
import br.com.lucolimac.shesafe.feature.auth.domain.repository.AuthRepository
import br.com.lucolimac.shesafe.feature.contacts.domain.model.SecureContact
import br.com.lucolimac.shesafe.feature.contacts.domain.repository.SecureContactRepository
import br.com.lucolimac.shesafe.feature.helprequests.domain.model.AlertDeliveryOutcome
import br.com.lucolimac.shesafe.feature.helprequests.domain.model.HelpRequest
import br.com.lucolimac.shesafe.feature.helprequests.domain.model.SmsProviderType
import br.com.lucolimac.shesafe.feature.helprequests.domain.repository.EmergencySmsGateway
import br.com.lucolimac.shesafe.feature.helprequests.domain.repository.HelpRequestRepository
import br.com.lucolimac.shesafe.feature.profile.domain.model.SettingKey
import br.com.lucolimac.shesafe.feature.profile.domain.model.UserSettings
import br.com.lucolimac.shesafe.feature.profile.domain.repository.ProfilePreferencesRepository
import br.com.lucolimac.shesafe.platform.LocationResult
import br.com.lucolimac.shesafe.platform.LocationService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FakeAuthRepository(
    initialUser: UserProfile? = null,
) : AuthRepository {
    private val userFlow = MutableStateFlow(initialUser)
    var signInResult: Resource<UserProfile>? = null
    var signOutError: AppError? = null

    fun setCurrentUser(user: UserProfile?) {
        userFlow.value = user
    }

    override fun observeCurrentUser(): Flow<UserProfile?> = userFlow.asStateFlow()

    override fun getCurrentUser(): UserProfile? = userFlow.value

    override suspend fun signInWithGoogleTokens(
        idToken: String,
        accessToken: String?,
    ): Resource<UserProfile> {
        val configured = signInResult
        if (configured != null) {
            if (configured is Resource.Success) {
                userFlow.value = configured.data
            }
            return configured
        }
        val profile = UserProfile(
            uid = "uid_${idToken.take(6)}",
            email = "jane@example.com",
            displayName = "Jane Doe",
            photoUrl = null,
        )
        userFlow.value = profile
        return Resource.Success(profile)
    }

    override suspend fun signOut(): Resource<Unit> {
        signOutError?.let { return Resource.Error(it) }
        userFlow.value = null
        return Resource.Success(Unit)
    }
}

class FakeSecureContactRepository(
    initialContacts: List<SecureContact> = emptyList(),
) : SecureContactRepository {
    private val contactsFlow = MutableStateFlow(initialContacts)
    var refreshError: AppError? = null
    var mutationError: AppError? = null

    fun setContacts(contacts: List<SecureContact>) {
        contactsFlow.value = contacts
    }

    override fun observeSecureContacts(): Flow<List<SecureContact>> = contactsFlow.asStateFlow()

    override suspend fun refreshSecureContacts(): Resource<List<SecureContact>> {
        refreshError?.let { return Resource.Error(it) }
        return Resource.Success(contactsFlow.value)
    }

    override suspend fun getSecureContactByPhone(phoneNumber: String): Resource<SecureContact> {
        val found = contactsFlow.value.firstOrNull { it.phoneNumber == phoneNumber }
        return if (found != null) {
            Resource.Success(found)
        } else {
            Resource.Error(AppError.Database.NotFound)
        }
    }

    override suspend fun registerSecureContact(contact: SecureContact): Resource<Unit> {
        mutationError?.let { return Resource.Error(it) }
        contactsFlow.update { current ->
            current.filterNot { it.phoneNumber == contact.phoneNumber } + contact
        }
        return Resource.Success(Unit)
    }

    override suspend fun updateSecureContact(
        originalPhoneNumber: String,
        contact: SecureContact,
    ): Resource<Unit> {
        mutationError?.let { return Resource.Error(it) }
        contactsFlow.update { current ->
            current.filterNot {
                it.phoneNumber == originalPhoneNumber || it.phoneNumber == contact.phoneNumber
            } + contact
        }
        return Resource.Success(Unit)
    }

    override suspend fun deleteSecureContact(phoneNumber: String): Resource<Unit> {
        mutationError?.let { return Resource.Error(it) }
        contactsFlow.update { current ->
            current.filterNot { it.phoneNumber == phoneNumber }
        }
        return Resource.Success(Unit)
    }
}

class FakeHelpRequestRepository(
    initialRequests: List<HelpRequest> = emptyList(),
) : HelpRequestRepository {
    private val requestsFlow = MutableStateFlow(initialRequests)
    var refreshError: AppError? = null
    var registerError: AppError? = null

    val recordedRequests: List<HelpRequest>
        get() = requestsFlow.value

    override fun observeHelpRequests(): Flow<List<HelpRequest>> = requestsFlow.asStateFlow()

    override suspend fun refreshHelpRequests(): Resource<List<HelpRequest>> {
        refreshError?.let { return Resource.Error(it) }
        return Resource.Success(requestsFlow.value)
    }

    override suspend fun registerHelpRequest(helpRequest: HelpRequest): Resource<Unit> {
        registerError?.let { return Resource.Error(it) }
        requestsFlow.update { listOf(helpRequest) + it }
        return Resource.Success(Unit)
    }
}

class FakeEmergencySmsGateway(
    var nextResult: Resource<AlertDeliveryOutcome> = Resource.Success(
        AlertDeliveryOutcome.SentViaGateway(SmsProviderType.SMS_DEV, 1),
    ),
) : EmergencySmsGateway {
    var lastContacts: List<SecureContact> = emptyList()
        private set
    var lastMessage: String? = null
        private set
    var callCount: Int = 0
        private set

    override suspend fun sendEmergencyAlert(
        contacts: List<SecureContact>,
        message: String,
    ): Resource<AlertDeliveryOutcome> {
        callCount += 1
        lastContacts = contacts
        lastMessage = message
        return nextResult
    }
}

class FakeProfilePreferencesRepository(
    initialMessage: String = "",
    initialSettings: UserSettings = UserSettings(),
) : ProfilePreferencesRepository {
    private val messageFlow = MutableStateFlow(initialMessage)
    private val settingsFlow = MutableStateFlow(initialSettings)
    var mutationError: AppError? = null

    val currentSettings: UserSettings
        get() = settingsFlow.value

    val currentMessage: String
        get() = messageFlow.value

    override fun observeHelpMessage(): Flow<String> = messageFlow.asStateFlow()

    override suspend fun refreshHelpMessage(): Resource<String> = Resource.Success(messageFlow.value)

    override suspend fun updateHelpMessage(message: String): Resource<Unit> {
        mutationError?.let { return Resource.Error(it) }
        messageFlow.value = message
        return Resource.Success(Unit)
    }

    override fun observeSettings(): Flow<UserSettings> = settingsFlow.asStateFlow()

    override suspend fun refreshSettings(): Resource<UserSettings> =
        Resource.Success(settingsFlow.value)

    override suspend fun updateSetting(key: SettingKey, enabled: Boolean): Resource<Unit> {
        mutationError?.let { return Resource.Error(it) }
        settingsFlow.update { current ->
            UserSettings(values = current.values + (key to enabled))
        }
        return Resource.Success(Unit)
    }
}

class FakeLocationService(
    var result: LocationResult = LocationResult.Unavailable,
) : LocationService {
    override fun requestCurrentLocation(onResult: (LocationResult) -> Unit) {
        onResult(result)
    }
}
