package br.com.lucolimac.shesafe.feature.helprequests.domain.usecase

import br.com.lucolimac.shesafe.core.domain.event.AppEvent
import br.com.lucolimac.shesafe.core.domain.event.AppEventBus
import br.com.lucolimac.shesafe.core.domain.model.AppError
import br.com.lucolimac.shesafe.core.domain.model.Resource
import br.com.lucolimac.shesafe.core.domain.model.onSuccess
import br.com.lucolimac.shesafe.feature.contacts.domain.model.SecureContact
import br.com.lucolimac.shesafe.feature.helprequests.domain.model.AlertDeliveryOutcome
import br.com.lucolimac.shesafe.feature.helprequests.domain.model.HelpRequest
import br.com.lucolimac.shesafe.feature.helprequests.domain.repository.EmergencySmsGateway
import br.com.lucolimac.shesafe.feature.helprequests.domain.repository.HelpRequestRepository
import br.com.lucolimac.shesafe.platform.HelpRequestComposer
import br.com.lucolimac.shesafe.platform.LocationCoordinates
import kotlinx.coroutines.flow.Flow
import kotlin.time.Clock

class ObserveHelpRequestsUseCase(
    private val repository: HelpRequestRepository,
) {
    operator fun invoke(): Flow<List<HelpRequest>> = repository.observeHelpRequests()
}

class RefreshHelpRequestsUseCase(
    private val repository: HelpRequestRepository,
) {
    suspend operator fun invoke(): Resource<List<HelpRequest>> = repository.refreshHelpRequests()
}

class TriggerEmergencyAlertUseCase(
    private val smsGateway: EmergencySmsGateway,
    private val helpRequestRepository: HelpRequestRepository,
    private val eventBus: AppEventBus,
) {
    suspend operator fun invoke(
        contacts: List<SecureContact>,
        baseMessage: String,
        location: LocationCoordinates?,
    ): Resource<AlertDeliveryOutcome> {
        if (contacts.isEmpty()) {
            return Resource.Error(AppError.Delivery.NoRecipients)
        }
        val composedMessage = HelpRequestComposer.composeMessage(
            message = baseMessage,
            location = location,
        )
        return smsGateway.sendEmergencyAlert(
            contacts = contacts,
            message = composedMessage,
        ).onSuccess {
            val helpRequest = HelpRequest(
                id = "local_${Clock.System.now().toEpochMilliseconds()}",
                phoneNumbers = contacts.map { it.phoneNumber },
                location = location,
                createdAt = Clock.System.now(),
            )
            helpRequestRepository.registerHelpRequest(helpRequest)
            eventBus.emit(AppEvent.HelpRequestsChanged)
        }
    }
}
