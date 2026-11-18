package br.com.lucolimac.shesafe.feature.helprequests.domain.repository

import br.com.lucolimac.shesafe.core.domain.model.Resource
import br.com.lucolimac.shesafe.feature.contacts.domain.model.SecureContact
import br.com.lucolimac.shesafe.feature.helprequests.domain.model.AlertDeliveryOutcome
import br.com.lucolimac.shesafe.feature.helprequests.domain.model.HelpRequest
import kotlinx.coroutines.flow.Flow

interface HelpRequestRepository {
    fun observeHelpRequests(): Flow<List<HelpRequest>>
    suspend fun refreshHelpRequests(): Resource<List<HelpRequest>>
    suspend fun registerHelpRequest(helpRequest: HelpRequest): Resource<Unit>
}

interface EmergencySmsGateway {
    suspend fun sendEmergencyAlert(
        contacts: List<SecureContact>,
        message: String,
    ): Resource<AlertDeliveryOutcome>
}
