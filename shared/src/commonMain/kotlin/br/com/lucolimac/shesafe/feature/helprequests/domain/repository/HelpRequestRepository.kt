package br.com.lucolimac.shesafe.feature.helprequests.domain.repository

import br.com.lucolimac.shesafe.core.domain.model.Resource
import br.com.lucolimac.shesafe.feature.contacts.domain.model.SecureContact
import br.com.lucolimac.shesafe.feature.helprequests.domain.model.AlertDeliveryOutcome
import br.com.lucolimac.shesafe.feature.helprequests.domain.model.HelpRequest
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for observing, synchronizing, and recording the user's [HelpRequest] history.
 */
interface HelpRequestRepository {
    /**
     * Observes the cached list of [HelpRequest] entries for the currently authenticated user.
     *
     * @return A cold or hot [Flow] emitting updates whenever stored help requests change.
     */
    fun observeHelpRequests(): Flow<List<HelpRequest>>

    /**
     * Synchronizes the local help request cache with the remote backend for the current user.
     *
     * @return A [Resource] containing the latest [HelpRequest] list or an error on failure.
     */
    suspend fun refreshHelpRequests(): Resource<List<HelpRequest>>

    /**
     * Persists a newly dispatched [helpRequest] locally and schedules or performs remote synchronization.
     *
     * @param helpRequest The emergency help request to record.
     * @return A [Resource] indicating success or persistence failure.
     */
    suspend fun registerHelpRequest(helpRequest: HelpRequest): Resource<Unit>
}

/**
 * Gateway contract responsible for delivering emergency SOS SMS messages to trusted contacts.
 */
interface EmergencySmsGateway {
    /**
     * Dispatches [message] to the given [contacts] via remote HTTP SMS or native device fallback.
     *
     * @param contacts Trusted contacts who should receive the emergency alert.
     * @param message Composed emergency text message, optionally including a location link.
     * @return A [Resource] containing the [AlertDeliveryOutcome] or a delivery error.
     */
    suspend fun sendEmergencyAlert(
        contacts: List<SecureContact>,
        message: String,
    ): Resource<AlertDeliveryOutcome>
}
