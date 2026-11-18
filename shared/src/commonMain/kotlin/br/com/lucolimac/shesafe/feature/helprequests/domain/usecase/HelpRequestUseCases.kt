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

/**
 * Use case that streams the authenticated user's recorded [HelpRequest] history.
 *
 * @param repository Data source providing observable help request records.
 */
class ObserveHelpRequestsUseCase(
    private val repository: HelpRequestRepository,
) {
    /**
     * Starts observing the user's [HelpRequest] list.
     *
     * @return A reactive [Flow] emitting the user's help requests whenever local storage updates.
     */
    operator fun invoke(): Flow<List<HelpRequest>> = repository.observeHelpRequests()
}

/**
 * Use case that triggers a remote synchronization of the authenticated user's [HelpRequest] history.
 *
 * @param repository Repository responsible for fetching remote help requests and updating local cache.
 */
class RefreshHelpRequestsUseCase(
    private val repository: HelpRequestRepository,
) {
    /**
     * Refreshes the user's help request history from the remote data source.
     *
     * @return A [Resource] containing the synchronized [HelpRequest] list or a domain [AppError].
     */
    suspend operator fun invoke(): Resource<List<HelpRequest>> = repository.refreshHelpRequests()
}

/**
 * Use case that composes an emergency SOS message, dispatches it through [EmergencySmsGateway],
 * records the resulting [HelpRequest] in history, and broadcasts [AppEvent.HelpRequestsChanged].
 *
 * @param smsGateway Gateway responsible for delivering the SMS alert to contacts.
 * @param helpRequestRepository Repository where successful help requests are logged.
 * @param eventBus Application-wide event bus notified after a help request is recorded.
 */
class TriggerEmergencyAlertUseCase(
    private val smsGateway: EmergencySmsGateway,
    private val helpRequestRepository: HelpRequestRepository,
    private val eventBus: AppEventBus,
) {
    /**
     * Composes and dispatches an emergency alert to [contacts], recording history upon successful delivery.
     *
     * @param contacts Trusted contacts to notify; must not be empty.
     * @param baseMessage User-configured or default SOS text before appending location coordinates.
     * @param location Optional geographic coordinates to embed as a Google Maps link.
     * @return A [Resource] describing the [AlertDeliveryOutcome], or [AppError.Delivery.NoRecipients]
     * if [contacts] is empty.
     */
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
