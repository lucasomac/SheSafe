package br.com.lucolimac.shesafe.android.domain.usecase

import br.com.lucolimac.shesafe.android.domain.entity.HelpRequest
import br.com.lucolimac.shesafe.android.domain.entity.SecureContact
import br.com.lucolimac.shesafe.android.domain.repository.HelpRequestRepository
import br.com.lucolimac.shesafe.android.domain.repository.SecureContactRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class HelpRequestUseCaseImpl(
    private val helpRequestRepository: HelpRequestRepository
) : HelpRequestUseCase {

    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
    override fun getHelpRequests(): Flow<List<HelpRequest>> {
        return flow {
            val helpRequests = helpRequestRepository.getHelpRequests()
            emit(helpRequests)
        }.flowOn(coroutineDispatcher)
    }

    override fun registerHelpRequest(helpRequest: HelpRequest): Flow<Boolean> {
        return flow {
            val result = helpRequestRepository.registerHelpRequest(helpRequest)
            emit(result)
        }.flowOn(coroutineDispatcher)
    }
}