package br.com.lucolimac.shesafe.presentation.viewmodel

import app.cash.turbine.test
import br.com.lucolimac.shesafe.core.domain.event.AppEventBusImpl
import br.com.lucolimac.shesafe.core.domain.model.AppError
import br.com.lucolimac.shesafe.core.domain.model.Resource
import br.com.lucolimac.shesafe.feature.contacts.domain.model.SecureContact
import br.com.lucolimac.shesafe.feature.contacts.domain.usecase.ObserveSecureContactsUseCase
import br.com.lucolimac.shesafe.feature.contacts.domain.usecase.RefreshSecureContactsUseCase
import br.com.lucolimac.shesafe.feature.helprequests.domain.model.AlertDeliveryOutcome
import br.com.lucolimac.shesafe.feature.helprequests.domain.usecase.TriggerEmergencyAlertUseCase
import br.com.lucolimac.shesafe.feature.home.presentation.viewmodel.HomeDialogType
import br.com.lucolimac.shesafe.feature.home.presentation.viewmodel.HomeEvent
import br.com.lucolimac.shesafe.feature.home.presentation.viewmodel.HomeViewModel
import br.com.lucolimac.shesafe.feature.profile.domain.model.SettingKey
import br.com.lucolimac.shesafe.feature.profile.domain.model.UserSettings
import br.com.lucolimac.shesafe.feature.profile.domain.usecase.ObserveHelpMessageUseCase
import br.com.lucolimac.shesafe.feature.profile.domain.usecase.ObserveSettingsUseCase
import br.com.lucolimac.shesafe.feature.profile.domain.usecase.RefreshHelpMessageUseCase
import br.com.lucolimac.shesafe.feature.profile.domain.usecase.RefreshSettingsUseCase
import br.com.lucolimac.shesafe.feature.profile.domain.usecase.UpdateSettingUseCase
import br.com.lucolimac.shesafe.platform.LocationCoordinates
import br.com.lucolimac.shesafe.platform.LocationResult
import br.com.lucolimac.shesafe.test.fakes.FakeEmergencySmsGateway
import br.com.lucolimac.shesafe.test.fakes.FakeHelpRequestRepository
import br.com.lucolimac.shesafe.test.fakes.FakeLocationService
import br.com.lucolimac.shesafe.test.fakes.FakeProfilePreferencesRepository
import br.com.lucolimac.shesafe.test.fakes.FakeSecureContactRepository
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import shesafe.shared.generated.resources.Res
import shesafe.shared.generated.resources.error_no_connection
import shesafe.shared.generated.resources.sms_sent
import shesafe.shared.generated.resources.sms_user_confirmation_required

/**
 * Unit tests verifying SOS dialog orchestration, confirmation preference persistence, and SMS
 * outcome mapping in [HomeViewModel].
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    /** Unconfined test dispatcher installed on `Dispatchers.Main` for synchronous coroutine execution. */
    private val testDispatcher = UnconfinedTestDispatcher()

    /**
     * Installs [testDispatcher] as `Dispatchers.Main` before each test runs.
     */
    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    /**
     * Restores `Dispatchers.Main` after each test completes.
     */
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Helper factory assembling a [HomeViewModel] wired with configurable in-memory test fakes.
     *
     * @param contactRepo Fake secure contacts repository.
     * @param prefsRepo Fake profile preferences repository.
     * @param gateway Fake emergency SMS gateway.
     * @param helpRepo Fake help request history repository.
     * @param locationService Fake platform location service.
     * @return Configured [HomeViewModel] under test.
     */
    private fun createViewModel(
        contactRepo: FakeSecureContactRepository = FakeSecureContactRepository(),
        prefsRepo: FakeProfilePreferencesRepository = FakeProfilePreferencesRepository(),
        gateway: FakeEmergencySmsGateway = FakeEmergencySmsGateway(),
        helpRepo: FakeHelpRequestRepository = FakeHelpRequestRepository(),
        locationService: FakeLocationService = FakeLocationService(),
    ): HomeViewModel {
        val eventBus = AppEventBusImpl()
        return HomeViewModel(
            observeSecureContactsUseCase = ObserveSecureContactsUseCase(contactRepo),
            refreshSecureContactsUseCase = RefreshSecureContactsUseCase(contactRepo),
            observeHelpMessageUseCase = ObserveHelpMessageUseCase(prefsRepo),
            refreshHelpMessageUseCase = RefreshHelpMessageUseCase(prefsRepo),
            observeSettingsUseCase = ObserveSettingsUseCase(prefsRepo),
            refreshSettingsUseCase = RefreshSettingsUseCase(prefsRepo),
            updateSettingUseCase = UpdateSettingUseCase(prefsRepo),
            triggerEmergencyAlertUseCase = TriggerEmergencyAlertUseCase(gateway, helpRepo, eventBus),
            locationService = locationService,
        )
    }

    /**
     * Verifies that tapping the SOS button with zero contacts opens [HomeDialogType.NO_CONTACTS]
     * and confirming navigates to contact registration.
     */
    @Test
    fun onRequestHelpClicked_whenNoContacts_showsNoContactsDialogAndNavigatesOnConfirm() = runTest {
        val viewModel = createViewModel()

        viewModel.onRequestHelpClicked("Default SOS")
        assertEquals(HomeDialogType.NO_CONTACTS, viewModel.uiState.value.activeDialog)

        viewModel.events.test {
            viewModel.onConfirmNoContactsDialog()
            assertEquals(HomeDialogType.NONE, viewModel.uiState.value.activeDialog)
            assertEquals(HomeEvent.NavigateToRegisterContact, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    /**
     * Verifies that confirming an alert with "don't ask again" checked disables
     * [SettingKey.SEND_CONFIRMATION], dispatches the emergency alert, and emits a success message.
     */
    @Test
    fun onConfirmSendAlert_withDontAskAgainChecked_persistsSettingAndSendsAlert() = runTest {
        val contacts = listOf(SecureContact(name = "Maria", phoneNumber = "11999998888"))
        val contactRepo = FakeSecureContactRepository(contacts)
        val prefsRepo = FakeProfilePreferencesRepository(
            initialMessage = "Alerta customizado",
            initialSettings = UserSettings(mapOf(SettingKey.SEND_CONFIRMATION to true)),
        )
        val gateway = FakeEmergencySmsGateway()
        val locationService = FakeLocationService(
            result = LocationResult.Available(LocationCoordinates(-23.5, -46.6)),
        )
        val viewModel = createViewModel(
            contactRepo = contactRepo,
            prefsRepo = prefsRepo,
            gateway = gateway,
            locationService = locationService,
        )

        viewModel.onRequestHelpClicked("Default SOS")
        assertEquals(HomeDialogType.CONFIRM_SEND, viewModel.uiState.value.activeDialog)

        viewModel.onDontAskAgainChanged(true)

        viewModel.events.test {
            viewModel.onConfirmSendAlert("Default SOS")
            val event = assertIs<HomeEvent.ShowMessage>(awaitItem())
            assertEquals(Res.string.sms_sent, event.messageRes)
            cancelAndIgnoreRemainingEvents()
        }

        assertFalse(prefsRepo.currentSettings.isEnabled(SettingKey.SEND_CONFIRMATION))
        assertEquals(1, gateway.callCount)
    }

    /**
     * Verifies that when [SettingKey.SEND_CONFIRMATION] is disabled, tapping SOS dispatches immediately
     * and maps both [AlertDeliveryOutcome.UserConfirmationRequired] and network errors to localized messages.
     */
    @Test
    fun onRequestHelpClicked_whenConfirmationDisabled_dispatchesImmediatelyAndMapsGatewayOutcome() = runTest {
        val contacts = listOf(SecureContact(name = "Maria", phoneNumber = "11999998888"))
        val prefsRepo = FakeProfilePreferencesRepository(
            initialSettings = UserSettings(mapOf(SettingKey.SEND_CONFIRMATION to false)),
        )
        val gateway = FakeEmergencySmsGateway(
            nextResult = Resource.Success(AlertDeliveryOutcome.UserConfirmationRequired),
        )
        val viewModel = createViewModel(
            contactRepo = FakeSecureContactRepository(contacts),
            prefsRepo = prefsRepo,
            gateway = gateway,
        )

        viewModel.events.test {
            viewModel.onRequestHelpClicked("Preciso de ajuda!")
            val confirmationEvent = assertIs<HomeEvent.ShowMessage>(awaitItem())
            assertEquals(Res.string.sms_user_confirmation_required, confirmationEvent.messageRes)

            gateway.nextResult = Resource.Error(AppError.Network.NoConnection)
            viewModel.onRequestHelpClicked("Preciso de ajuda!")
            val errorEvent = assertIs<HomeEvent.ShowMessage>(awaitItem())
            assertEquals(Res.string.error_no_connection, errorEvent.messageRes)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
