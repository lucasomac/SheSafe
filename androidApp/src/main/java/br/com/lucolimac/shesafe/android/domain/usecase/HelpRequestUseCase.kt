package br.com.lucolimac.shesafe.android.domain.usecase

import br.com.lucolimac.shesafe.android.domain.entity.HelpRequest
import kotlinx.coroutines.flow.Flow

interface HelpRequestUseCase {
    fun getHelpRequests(): Flow<List<HelpRequest>>
    fun registerHelpRequest(helpRequest: HelpRequest): Flow<Boolean>
}