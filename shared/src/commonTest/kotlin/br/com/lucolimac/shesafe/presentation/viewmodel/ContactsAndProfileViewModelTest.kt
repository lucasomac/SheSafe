package br.com.lucolimac.shesafe.presentation.viewmodel

import app.cash.turbine.test
import br.com.lucolimac.shesafe.core.domain.event.AppEventBusImpl
import br.com.lucolimac.shesafe.core.domain.model.AppError
import br.com.lucolimac.shesafe.feature.auth.domain.model.UserProfile
import br.com.lucolimac.shesafe.feature.auth.domain.usecase.GetCurrentUserUseCase
import br.com.lucolimac.shesafe.feature.auth.domain.usecase.ObserveCurrentUserUseCase
import br.com.lucolimac.shesafe.feature.auth.domain.usecase.SignInWithGoogleUseCase
import br.com.lucolimac.shesafe.feature.auth.domain.usecase.SignOutUseCase
import br.com.lucolimac.shesafe.feature.auth.presentation.viewmodel.AuthEvent
import br.com.lucolimac.shesafe.feature.auth.presentation.viewmodel.AuthViewModel
import br.com.lucolimac.shesafe.feature.contacts.domain.model.SecureContact
import br.com.lucolimac.shesafe.feature.contacts.domain.usecase.DeleteSecureContactUseCase
import br.com.lucolimac.shesafe.feature.contacts.domain.usecase.GetSecureContactByPhoneUseCase
import br.com.lucolimac.shesafe.feature.contacts.domain.usecase.ObserveSecureContactsUseCase
import br.com.lucolimac.shesafe.feature.contacts.domain.usecase.RefreshSecureContactsUseCase
import br.com.lucolimac.shesafe.feature.contacts.domain.usecase.SaveSecureContactUseCase
import br.com.lucolimac.shesafe.feature.contacts.presentation.viewmodel.RegisterContactEvent
import br.com.lucolimac.shesafe.feature.contacts.presentation.viewmodel.RegisterSecureContactViewModel
import br.com.lucolimac.shesafe.feature.contacts.presentation.viewmodel.SecureContactsViewModel
import br.com.lucolimac.shesafe.feature.helprequests.domain.usecase.ObserveHelpRequestsUseCase
import br.com.lucolimac.shesafe.feature.helprequests.domain.usecase.RefreshHelpRequestsUseCase
import br.com.lucolimac.shesafe.feature.profile.domain.model.SettingKey
import br.com.lucolimac.shesafe.feature.profile.domain.usecase.ObserveHelpMessageUseCase
import br.com.lucolimac.shesafe.feature.profile.domain.usecase.ObserveSettingsUseCase
import br.com.lucolimac.shesafe.feature.profile.domain.usecase.RefreshHelpMessageUseCase
import br.com.lucolimac.shesafe.feature.profile.domain.usecase.RefreshSettingsUseCase
import br.com.lucolimac.shesafe.feature.profile.domain.usecase.UpdateHelpMessageUseCase
import br.com.lucolimac.shesafe.feature.profile.domain.usecase.UpdateSettingUseCase
import br.com.lucolimac.shesafe.feature.profile.presentation.viewmodel.ProfileActiveSheet
import br.com.lucolimac.shesafe.feature.profile.presentation.viewmodel.ProfileEvent
import br.com.lucolimac.shesafe.feature.profile.presentation.viewmodel.ProfileViewModel
import br.com.lucolimac.shesafe.test.fakes.FakeAuthRepository
import br.com.lucolimac.shesafe.test.fakes.FakeHelpRequestRepository
import br.com.lucolimac.shesafe.test.fakes.FakeProfilePreferencesRepository
import br.com.lucolimac.shesafe.test.fakes.FakeSecureContactRepository
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import shesafe.shared.generated.resources.Res
import shesafe.shared.generated.resources.error_no_connection

/**
 * Unit tests covering state transitions, form validation, bottom sheet management, and navigation
 * events for [SecureContactsViewModel], [RegisterSecureContactViewModel], [ProfileViewModel], and
 * [AuthViewModel].
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ContactsAndProfileViewModelTest {

    /** Unconfined test dispatcher installed on `Dispatchers.Main` for deterministic ViewModel execution. */
    private val testDispatcher = UnconfinedTestDispatcher()

    /**
     * Installs [testDispatcher] as `Dispatchers.Main` prior to each test case.
     */
    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    /**
     * Resets `Dispatchers.Main` to its default state after each test case.
     */
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Verifies that [SecureContactsViewModel] surfaces a connection error resource when the cache is
     * empty and refresh fails, and clears the error once a retry succeeds.
     */
    @Test
    fun secureContactsViewModel_exposesErrorResourceWhenEmptyAndRefreshFails() = runTest {
        val repo = FakeSecureContactRepository().apply {
            refreshError = AppError.Network.NoConnection
        }
        val eventBus = AppEventBusImpl()
        val viewModel = SecureContactsViewModel(
            observeSecureContactsUseCase = ObserveSecureContactsUseCase(repo),
            refreshSecureContactsUseCase = RefreshSecureContactsUseCase(repo),
            deleteSecureContactUseCase = DeleteSecureContactUseCase(repo, eventBus),
        )

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(Res.string.error_no_connection, viewModel.uiState.value.errorResource)

        // Retry succeeds once connection recovers
        repo.refreshError = null
        repo.setContacts(listOf(SecureContact("Maria", "11999998888")))
        viewModel.refresh()

        assertNull(viewModel.uiState.value.errorResource)
        assertEquals(1, viewModel.uiState.value.contacts.size)
    }

    /**
     * Verifies that [SecureContactsViewModel] stages a contact for deletion and removes it upon confirmation.
     */
    @Test
    fun secureContactsViewModel_confirmDeleteRemovesPendingContact() = runTest {
        val maria = SecureContact("Maria", "11999998888")
        val repo = FakeSecureContactRepository(listOf(maria))
        val eventBus = AppEventBusImpl()
        val viewModel = SecureContactsViewModel(
            observeSecureContactsUseCase = ObserveSecureContactsUseCase(repo),
            refreshSecureContactsUseCase = RefreshSecureContactsUseCase(repo),
            deleteSecureContactUseCase = DeleteSecureContactUseCase(repo, eventBus),
        )

        viewModel.onRequestDeleteContact(maria)
        assertEquals(maria, viewModel.uiState.value.contactPendingDelete)

        viewModel.onConfirmDeleteContact()
        assertNull(viewModel.uiState.value.contactPendingDelete)
        assertTrue(viewModel.uiState.value.contacts.isEmpty())
    }

    /**
     * Verifies that [RegisterSecureContactViewModel] validates name and phone inputs in real time and
     * emits [RegisterContactEvent.NavigateBack] after saving a valid contact.
     */
    @Test
    fun registerSecureContactViewModel_validatesInputAndAwaitsSaveBeforeNavigateBack() = runTest {
        val repo = FakeSecureContactRepository()
        val eventBus = AppEventBusImpl()
        val viewModel = RegisterSecureContactViewModel(
            getSecureContactByPhoneUseCase = GetSecureContactByPhoneUseCase(repo),
            saveSecureContactUseCase = SaveSecureContactUseCase(repo, eventBus),
        )

        viewModel.initialize(null)
        viewModel.onNameChanged("")
        assertNotNull(viewModel.uiState.value.nameErrorRes)

        viewModel.onNameChanged("Maria Silva")
        viewModel.onPhoneChanged("11999")
        assertNotNull(viewModel.uiState.value.phoneErrorRes)
        assertFalse(viewModel.uiState.value.isFormValid)

        viewModel.onPhoneChanged("11999998888")
        assertNull(viewModel.uiState.value.phoneErrorRes)
        assertTrue(viewModel.uiState.value.isFormValid)

        viewModel.events.test {
            viewModel.onSaveClicked()
            assertEquals(RegisterContactEvent.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    /**
     * Verifies that [ProfileViewModel] opens and dismisses the edit-message sheet, updates custom
     * help messages and settings reactively, and emits [ProfileEvent.NavigateToLogin] on sign-out.
     */
    @Test
    fun profileViewModel_opensAndDismissesEditMessageSheetAndLogsOut() = runTest {
        val authRepo = FakeAuthRepository(
            initialUser = UserProfile("u1", "maria@example.com", "Maria Silva", null),
        )
        val prefsRepo = FakeProfilePreferencesRepository(initialMessage = "Mensagem inicial")
        val helpRepo = FakeHelpRequestRepository()
        val eventBus = AppEventBusImpl()

        val viewModel = ProfileViewModel(
            observeCurrentUserUseCase = ObserveCurrentUserUseCase(authRepo),
            observeHelpMessageUseCase = ObserveHelpMessageUseCase(prefsRepo),
            refreshHelpMessageUseCase = RefreshHelpMessageUseCase(prefsRepo),
            updateHelpMessageUseCase = UpdateHelpMessageUseCase(prefsRepo),
            observeSettingsUseCase = ObserveSettingsUseCase(prefsRepo),
            refreshSettingsUseCase = RefreshSettingsUseCase(prefsRepo),
            updateSettingUseCase = UpdateSettingUseCase(prefsRepo),
            observeHelpRequestsUseCase = ObserveHelpRequestsUseCase(helpRepo),
            refreshHelpRequestsUseCase = RefreshHelpRequestsUseCase(helpRepo),
            signOutUseCase = SignOutUseCase(authRepo, eventBus),
        )

        assertEquals("Maria Silva", viewModel.uiState.value.user?.displayName)
        assertEquals(ProfileActiveSheet.NONE, viewModel.uiState.value.activeSheet)

        // Verify bug fix: opening and dismissing EDIT_MESSAGE sheet properly resets activeSheet to NONE
        viewModel.onOpenEditMessageSheet()
        assertEquals(ProfileActiveSheet.EDIT_MESSAGE, viewModel.uiState.value.activeSheet)
        viewModel.onDismissSheet()
        assertEquals(ProfileActiveSheet.NONE, viewModel.uiState.value.activeSheet)

        // Save new message closes sheet and updates state reactively
        viewModel.onOpenEditMessageSheet()
        viewModel.onSaveHelpMessage("Nova mensagem de alerta")
        assertEquals(ProfileActiveSheet.NONE, viewModel.uiState.value.activeSheet)
        assertEquals("Nova mensagem de alerta", viewModel.uiState.value.customHelpMessage)

        // Toggle setting
        viewModel.onToggleSetting(SettingKey.SEND_CONFIRMATION, false)
        assertFalse(viewModel.uiState.value.settings.isEnabled(SettingKey.SEND_CONFIRMATION))

        // Logout emits NavigateToLogin
        viewModel.events.test {
            viewModel.onLogoutClicked()
            assertEquals(ProfileEvent.NavigateToLogin, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    /**
     * Verifies that [AuthViewModel] updates authentication state and emits [AuthEvent.NavigateToHome]
     * upon receiving valid Google OAuth tokens.
     */
    @Test
    fun authViewModel_emitsNavigateToHomeWhenUserSignsIn() = runTest {
        val authRepo = FakeAuthRepository(initialUser = null)
        val eventBus = AppEventBusImpl()
        val viewModel = AuthViewModel(
            observeCurrentUserUseCase = ObserveCurrentUserUseCase(authRepo),
            getCurrentUserUseCase = GetCurrentUserUseCase(authRepo),
            signInWithGoogleUseCase = SignInWithGoogleUseCase(authRepo, eventBus),
        )

        assertFalse(viewModel.uiState.value.isAuthenticated)

        viewModel.events.test {
            viewModel.onGoogleTokensReceived(idToken = "token123456", accessToken = "acc")
            assertEquals(AuthEvent.NavigateToHome, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        assertTrue(viewModel.uiState.value.isAuthenticated)
    }
}
