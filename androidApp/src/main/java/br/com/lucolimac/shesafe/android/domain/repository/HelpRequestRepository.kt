package br.com.lucolimac.shesafe.android.domain.repository

import br.com.lucolimac.shesafe.android.domain.entity.HelpRequest

interface HelpRequestRepository {
    suspend fun getHelpRequests(): List<HelpRequest>
    suspend fun registerHelpRequest(helpRequest: HelpRequest): Boolean
}