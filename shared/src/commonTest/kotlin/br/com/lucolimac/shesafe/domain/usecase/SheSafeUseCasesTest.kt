package br.com.lucolimac.shesafe.domain.usecase

import app.cash.turbine.test
import br.com.lucolimac.shesafe.core.domain.event.AppEvent
import br.com.lucolimac.shesafe.core.domain.event.AppEventBusImpl
import br.com.lucolimac.shesafe.core.domain.model.AppError
import br.com.lucolimac.shesafe.core.domain.model.Resource
import br.com.lucolimac.shesafe.feature.auth.domain.model.UserProfile
import br.com.lucolimac.shesafe.feature.auth.domain.usecase.SignOutUseCase
import br.com.lucolimac.shesafe.feature.contacts.domain.model.SecureContact
import br.com.lucolimac.shesafe.feature.contacts.domain.usecase.DeleteSecureContactUseCase
import br.com.lucolimac.shesafe.feature.contacts.domain.usecase.SaveSecureContactUseCase
import br.com.lucolimac.shesafe.feature.helprequests.domain.usecase.TriggerEmergencyAlertUseCase
import br.com.lucolimac.shesafe.feature.profile.domain.usecase.UpdateHelpMessageUseCase
import br.com.lucolimac.shesafe.platform.LocationCoordinates
import br.com.lucolimac.shesafe.test.fakes.FakeAuthRepository
import br.com.lucolimac.shesafe.test.fakes.FakeEmergencySmsGateway
import br.com.lucolimac.shesafe.test.fakes.FakeHelpRequestRepository
import br.com.lucolimac.shesafe.test.fakes.FakeProfilePreferencesRepository
import br.com.lucolimac.shesafe.test.fakes.FakeSecureContactRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class SheSafeUseCasesTest {

    @Test
    fun saveSecureContact_rejectsBlankName() = runTest {
        val repo = FakeSecureContactRepository()
        val eventBus = AppEventBusImpl()
        val useCase = SaveSecureContactUseCase(repo, eventBus)

        val result = useCase(name = "   ", phoneNumber = "11999998888")

        val error = assertIs<Resource.Error>(result)
        assertEquals(AppError.Validation.EmptyName, error.error)
    }

    @Test
    fun saveSecureContact_rejectsInvalidPhoneLength() = runTest {
        val repo = FakeSecureContactRepository()
        val eventBus = AppEventBusImpl()
        val useCase = SaveSecureContactUseCase(repo, eventBus)

        val result = useCase(name = "Maria", phoneNumber = "11999")

        val error = assertIs<Resource.Error>(result)
        assertEquals(AppError.Validation.InvalidPhoneDigits, error.error)
    }

    @Test
    fun saveSecureContact_normalizesDigitsAndEmitsContactsChanged() = runTest {
        val repo = FakeSecureContactRepository()
        val eventBus = AppEventBusImpl()
        val useCase = SaveSecureContactUseCase(repo, eventBus)

        eventBus.events.test {
            val result = useCase(name = "  Maria Silva ", phoneNumber = "(11) 99999-8888")
            assertIs<Resource.Success<Unit>>(result)
            assertEquals(AppEvent.ContactsChanged, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        val saved = assertIs<Resource.Success<SecureContact>>(
            repo.getSecureContactByPhone("11999998888"),
        )
        assertEquals("Maria Silva", saved.data.name)
    }

    @Test
    fun deleteSecureContact_removesContactAndEmitsContactsChanged() = runTest {
        val initial = SecureContact(name = "Ana", phoneNumber = "21988887777")
        val repo = FakeSecureContactRepository(listOf(initial))
        val eventBus = AppEventBusImpl()
        val useCase = DeleteSecureContactUseCase(repo, eventBus)

        eventBus.events.test {
            val result = useCase(phoneNumber = "(21) 98888-7777")
            assertIs<Resource.Success<Unit>>(result)
            assertEquals(AppEvent.ContactsChanged, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        assertIs<Resource.Error>(repo.getSecureContactByPhone("21988887777"))
    }

    @Test
    fun triggerEmergencyAlert_returnsNoRecipientsWhenContactsEmpty() = runTest {
        val gateway = FakeEmergencySmsGateway()
        val helpRepo = FakeHelpRequestRepository()
        val eventBus = AppEventBusImpl()
        val useCase = TriggerEmergencyAlertUseCase(gateway, helpRepo, eventBus)

        val result = useCase(
            contacts = emptyList(),
            baseMessage = "Help me!",
            location = null,
        )

        val error = assertIs<Resource.Error>(result)
        assertEquals(AppError.Delivery.NoRecipients, error.error)
        assertEquals(0, gateway.callCount)
        assertTrue(helpRepo.recordedRequests.isEmpty())
    }

    @Test
    fun triggerEmergencyAlert_composesLocationRecordsHistoryAndEmitsEvent() = runTest {
        val gateway = FakeEmergencySmsGateway()
        val helpRepo = FakeHelpRequestRepository()
        val eventBus = AppEventBusImpl()
        val useCase = TriggerEmergencyAlertUseCase(gateway, helpRepo, eventBus)
        val contacts = listOf(SecureContact(name = "Maria", phoneNumber = "11999998888"))
        val coords = LocationCoordinates(latitude = -23.55052, longitude = -46.633308)

        eventBus.events.test {
            val result = useCase(
                contacts = contacts,
                baseMessage = "Preciso de ajuda!",
                location = coords,
            )
            assertIs<Resource.Success<*>>(result)
            assertEquals(AppEvent.HelpRequestsChanged, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        val sentText = gateway.lastMessage.orEmpty()
        assertTrue(sentText.contains("Preciso de ajuda!"))
        assertTrue(sentText.contains("-23.55052,-46.633308"))
        assertEquals(1, helpRepo.recordedRequests.size)
        assertEquals(coords, helpRepo.recordedRequests.first().location)
    }

    @Test
    fun triggerEmergencyAlert_withoutCoordinatesOmitsNullIslandLink() = runTest {
        val gateway = FakeEmergencySmsGateway()
        val helpRepo = FakeHelpRequestRepository()
        val eventBus = AppEventBusImpl()
        val useCase = TriggerEmergencyAlertUseCase(gateway, helpRepo, eventBus)
        val contacts = listOf(SecureContact(name = "Maria", phoneNumber = "11999998888"))

        val result = useCase(
            contacts = contacts,
            baseMessage = "Preciso de ajuda!",
            location = null,
        )

        assertIs<Resource.Success<*>>(result)
        assertEquals("Preciso de ajuda!", gateway.lastMessage)
        assertNull(helpRepo.recordedRequests.first().location)
    }

    @Test
    fun updateHelpMessage_rejectsBlankInputAndTrimsValidInput() = runTest {
        val repo = FakeProfilePreferencesRepository()
        val useCase = UpdateHelpMessageUseCase(repo)

        val blankResult = useCase("   ")
        val error = assertIs<Resource.Error>(blankResult)
        assertEquals(AppError.Validation.EmptyMessage, error.error)

        val okResult = useCase("  Socorro urgente!  ")
        assertIs<Resource.Success<Unit>>(okResult)
        assertEquals("Socorro urgente!", repo.currentMessage)
    }

    @Test
    fun signOut_clearsSessionAndEmitsUserLoggedOut() = runTest {
        val authRepo = FakeAuthRepository(
            initialUser = UserProfile("u1", "maria@example.com", "Maria", null),
        )
        val eventBus = AppEventBusImpl()
        val useCase = SignOutUseCase(authRepo, eventBus)

        eventBus.events.test {
            val result = useCase()
            assertIs<Resource.Success<Unit>>(result)
            assertEquals(AppEvent.UserLoggedOut, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        assertNull(authRepo.getCurrentUser())
    }
}
