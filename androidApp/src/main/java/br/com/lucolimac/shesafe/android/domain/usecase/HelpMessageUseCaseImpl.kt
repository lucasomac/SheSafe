package br.com.lucolimac.shesafe.android.domain.usecase

import br.com.lucolimac.shesafe.android.domain.repository.HelpMessageRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class HelpMessageUseCaseImpl(private val repository: HelpMessageRepository) : HelpMessageUseCase {
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
    override suspend fun getHelpMessages(): Flow<String> {
        return flow {
            val messages = repository.getHelpMessages()
            emit(messages)
        }.flowOn(coroutineDispatcher)
    }

    override suspend fun registerHelpMessage(message: String): Flow<Boolean> {
        return flow {
            val result = repository.registerHelpMessage(message)
            emit(result)
        }.flowOn(coroutineDispatcher)
    }
}