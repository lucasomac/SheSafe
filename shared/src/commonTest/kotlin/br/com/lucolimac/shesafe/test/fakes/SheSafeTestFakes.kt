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

/**
 * In-memory test fake for [AuthRepository] supporting controllable sign-in and sign-out outcomes.
 *
 * @param initialUser Optional [UserProfile] seeded as the initially authenticated user.
 */
class FakeAuthRepository(
    initialUser: UserProfile? = null,
) : AuthRepository {
    /** Backing state flow holding the active [UserProfile] or `null`. */
    private val userFlow = MutableStateFlow(initialUser)

    /** Optional canned [Resource] returned by [signInWithGoogleTokens] when non-null. */
    var signInResult: Resource<UserProfile>? = null

    /** Optional [AppError] returned by [signOut] to simulate sign-out failures. */
    var signOutError: AppError? = null

    /**
     * Directly updates the active [user] emitted by [observeCurrentUser].
     *
     * @param user New [UserProfile] or `null` to simulate an unauthenticated state.
     */
    fun setCurrentUser(user: UserProfile?) {
        userFlow.value = user
    }

    /**
     * Emits the current [UserProfile] state flow.
     *
     * @return Reactive [Flow] of the active [UserProfile] or `null`.
     */
    override fun observeCurrentUser(): Flow<UserProfile?> = userFlow.asStateFlow()

    /**
     * Returns the latest [UserProfile] snapshot synchronously.
     *
     * @return Current [UserProfile] or `null` if signed out.
     */
    override fun getCurrentUser(): UserProfile? = userFlow.value

    /**
     * Simulates Google token sign-in using [signInResult] if configured, or synthesizes a default profile.
     *
     * @param idToken Google OpenID Connect ID token.
     * @param accessToken Optional Google OAuth access token.
     * @return Configured or synthesized [Resource] containing the signed-in [UserProfile].
     */
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

    /**
     * Clears the active user unless [signOutError] is configured.
     *
     * @return [Resource.Success] when signed out, or [Resource.Error] wrapping [signOutError].
     */
    override suspend fun signOut(): Resource<Unit> {
        signOutError?.let { return Resource.Error(it) }
        userFlow.value = null
        return Resource.Success(Unit)
    }
}

/**
 * In-memory test fake for [SecureContactRepository] supporting controllable refresh and mutation errors.
 *
 * @param initialContacts Initial list of [SecureContact] entries seeded into the fake store.
 */
class FakeSecureContactRepository(
    initialContacts: List<SecureContact> = emptyList(),
) : SecureContactRepository {
    /** Backing state flow storing the current list of [SecureContact] items. */
    private val contactsFlow = MutableStateFlow(initialContacts)

    /** Optional [AppError] returned by [refreshSecureContacts] when non-null. */
    var refreshError: AppError? = null

    /** Optional [AppError] returned by write/delete operations when non-null. */
    var mutationError: AppError? = null

    /**
     * Replaces the in-memory contact list emitted by [observeSecureContacts].
     *
     * @param contacts New list of [SecureContact] items.
     */
    fun setContacts(contacts: List<SecureContact>) {
        contactsFlow.value = contacts
    }

    /**
     * Observes the in-memory [SecureContact] list.
     *
     * @return Reactive [Flow] emitting current contacts.
     */
    override fun observeSecureContacts(): Flow<List<SecureContact>> = contactsFlow.asStateFlow()

    /**
     * Simulates refreshing contacts from remote storage, failing if [refreshError] is set.
     *
     * @return [Resource.Success] with current contacts, or [Resource.Error] wrapping [refreshError].
     */
    override suspend fun refreshSecureContacts(): Resource<List<SecureContact>> {
        refreshError?.let { return Resource.Error(it) }
        return Resource.Success(contactsFlow.value)
    }

    /**
     * Looks up a [SecureContact] matching [phoneNumber] in memory.
     *
     * @param phoneNumber Normalized phone number to query.
     * @return [Resource.Success] if found, or [AppError.Database.NotFound] otherwise.
     */
    override suspend fun getSecureContactByPhone(phoneNumber: String): Resource<SecureContact> {
        val found = contactsFlow.value.firstOrNull { it.phoneNumber == phoneNumber }
        return if (found != null) {
            Resource.Success(found)
        } else {
            Resource.Error(AppError.Database.NotFound)
        }
    }

    /**
     * Inserts or replaces [contact] by phone number unless [mutationError] is set.
     *
     * @param contact The [SecureContact] to store.
     * @return [Resource.Success] or [Resource.Error] wrapping [mutationError].
     */
    override suspend fun registerSecureContact(contact: SecureContact): Resource<Unit> {
        mutationError?.let { return Resource.Error(it) }
        contactsFlow.update { current ->
            current.filterNot { it.phoneNumber == contact.phoneNumber } + contact
        }
        return Resource.Success(Unit)
    }

    /**
     * Replaces the entry matching [originalPhoneNumber] with [contact] unless [mutationError] is set.
     *
     * @param originalPhoneNumber Previous phone number identifying the contact to update.
     * @param contact Updated [SecureContact] data.
     * @return [Resource.Success] or [Resource.Error] wrapping [mutationError].
     */
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

    /**
     * Removes any contact matching [phoneNumber] unless [mutationError] is set.
     *
     * @param phoneNumber Phone number of the contact to remove.
     * @return [Resource.Success] or [Resource.Error] wrapping [mutationError].
     */
    override suspend fun deleteSecureContact(phoneNumber: String): Resource<Unit> {
        mutationError?.let { return Resource.Error(it) }
        contactsFlow.update { current ->
            current.filterNot { it.phoneNumber == phoneNumber }
        }
        return Resource.Success(Unit)
    }
}

/**
 * In-memory test fake for [HelpRequestRepository] recording registered [HelpRequest] entries.
 *
 * @param initialRequests Initial [HelpRequest] items seeded into the repository.
 */
class FakeHelpRequestRepository(
    initialRequests: List<HelpRequest> = emptyList(),
) : HelpRequestRepository {
    /** Backing state flow holding stored [HelpRequest] records. */
    private val requestsFlow = MutableStateFlow(initialRequests)

    /** Optional [AppError] returned by [refreshHelpRequests] when non-null. */
    var refreshError: AppError? = null

    /** Optional [AppError] returned by [registerHelpRequest] when non-null. */
    var registerError: AppError? = null

    /** Snapshot of all [HelpRequest] items currently stored in memory, ordered newest-first. */
    val recordedRequests: List<HelpRequest>
        get() = requestsFlow.value

    /**
     * Observes the in-memory [HelpRequest] list.
     *
     * @return Reactive [Flow] emitting stored help requests.
     */
    override fun observeHelpRequests(): Flow<List<HelpRequest>> = requestsFlow.asStateFlow()

    /**
     * Simulates remote synchronization, returning [refreshError] if configured.
     *
     * @return [Resource.Success] with current requests, or [Resource.Error] wrapping [refreshError].
     */
    override suspend fun refreshHelpRequests(): Resource<List<HelpRequest>> {
        refreshError?.let { return Resource.Error(it) }
        return Resource.Success(requestsFlow.value)
    }

    /**
     * Prepends [helpRequest] to the in-memory list unless [registerError] is set.
     *
     * @param helpRequest New [HelpRequest] to record.
     * @return [Resource.Success] or [Resource.Error] wrapping [registerError].
     */
    override suspend fun registerHelpRequest(helpRequest: HelpRequest): Resource<Unit> {
        registerError?.let { return Resource.Error(it) }
        requestsFlow.update { listOf(helpRequest) + it }
        return Resource.Success(Unit)
    }
}

/**
 * Spy/fake implementation of [EmergencySmsGateway] capturing invocation arguments and returning
 * a configurable [nextResult].
 *
 * @property nextResult [Resource] returned on subsequent [sendEmergencyAlert] invocations.
 */
class FakeEmergencySmsGateway(
    var nextResult: Resource<AlertDeliveryOutcome> = Resource.Success(
        AlertDeliveryOutcome.SentViaGateway(SmsProviderType.SMS_DEV, 1),
    ),
) : EmergencySmsGateway {
    /** Contacts passed to the most recent [sendEmergencyAlert] invocation. */
    var lastContacts: List<SecureContact> = emptyList()
        private set

    /** Message string passed to the most recent [sendEmergencyAlert] invocation. */
    var lastMessage: String? = null
        private set

    /** Total number of times [sendEmergencyAlert] has been invoked. */
    var callCount: Int = 0
        private set

    /**
     * Records [contacts] and [message], increments [callCount], and returns [nextResult].
     *
     * @param contacts Contacts targeted by the alert.
     * @param message Composed emergency message body.
     * @return Configured [nextResult].
     */
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

/**
 * In-memory test fake for [ProfilePreferencesRepository] storing custom help messages and [UserSettings].
 *
 * @param initialMessage Initial custom SOS message seeded into memory.
 * @param initialSettings Initial [UserSettings] seeded into memory.
 */
class FakeProfilePreferencesRepository(
    initialMessage: String = "",
    initialSettings: UserSettings = UserSettings(),
) : ProfilePreferencesRepository {
    /** Backing state flow holding the custom SOS message string. */
    private val messageFlow = MutableStateFlow(initialMessage)

    /** Backing state flow holding the current [UserSettings]. */
    private val settingsFlow = MutableStateFlow(initialSettings)

    /** Optional [AppError] returned by [updateHelpMessage] and [updateSetting] when non-null. */
    var mutationError: AppError? = null

    /** Latest [UserSettings] snapshot stored in memory. */
    val currentSettings: UserSettings
        get() = settingsFlow.value

    /** Latest custom SOS help message stored in memory. */
    val currentMessage: String
        get() = messageFlow.value

    /**
     * Observes the in-memory custom help message.
     *
     * @return Reactive [Flow] emitting the stored message string.
     */
    override fun observeHelpMessage(): Flow<String> = messageFlow.asStateFlow()

    /**
     * Returns the current in-memory help message wrapped in [Resource.Success].
     *
     * @return [Resource.Success] containing [currentMessage].
     */
    override suspend fun refreshHelpMessage(): Resource<String> = Resource.Success(messageFlow.value)

    /**
     * Updates the stored help [message] unless [mutationError] is configured.
     *
     * @param message New custom SOS message text.
     * @return [Resource.Success] or [Resource.Error] wrapping [mutationError].
     */
    override suspend fun updateHelpMessage(message: String): Resource<Unit> {
        mutationError?.let { return Resource.Error(it) }
        messageFlow.value = message
        return Resource.Success(Unit)
    }

    /**
     * Observes the in-memory [UserSettings].
     *
     * @return Reactive [Flow] emitting [UserSettings] updates.
     */
    override fun observeSettings(): Flow<UserSettings> = settingsFlow.asStateFlow()

    /**
     * Returns the current in-memory [UserSettings] wrapped in [Resource.Success].
     *
     * @return [Resource.Success] containing [currentSettings].
     */
    override suspend fun refreshSettings(): Resource<UserSettings> =
        Resource.Success(settingsFlow.value)

    /**
     * Updates [key] to [enabled] in memory unless [mutationError] is configured.
     *
     * @param key Target [SettingKey] to update.
     * @param enabled Desired boolean value.
     * @return [Resource.Success] or [Resource.Error] wrapping [mutationError].
     */
    override suspend fun updateSetting(key: SettingKey, enabled: Boolean): Resource<Unit> {
        mutationError?.let { return Resource.Error(it) }
        settingsFlow.update { current ->
            UserSettings(values = current.values + (key to enabled))
        }
        return Resource.Success(Unit)
    }
}

/**
 * Synchronous test fake for [LocationService] immediately invoking callbacks with [result].
 *
 * @property result Canned [LocationResult] delivered to callers of [requestCurrentLocation].
 */
class FakeLocationService(
    var result: LocationResult = LocationResult.Unavailable,
) : LocationService {
    /**
     * Immediately invokes [onResult] with the configured [result].
     *
     * @param onResult Callback receiving the simulated [LocationResult].
     */
    override fun requestCurrentLocation(onResult: (LocationResult) -> Unit) {
        onResult(result)
    }
}
