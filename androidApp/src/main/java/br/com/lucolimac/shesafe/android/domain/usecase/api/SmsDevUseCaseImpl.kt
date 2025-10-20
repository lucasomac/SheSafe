package br.com.lucolimac.shesafe.android.domain.usecase.api

import br.com.lucolimac.shesafe.android.domain.entity.SmsDevBody
import br.com.lucolimac.shesafe.android.domain.entity.SmsDevEntity
import br.com.lucolimac.shesafe.android.domain.repository.api.SmsDevRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class SmsDevUseCaseImpl(
    private val repository: SmsDevRepository
) : SmsDevUseCase {
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
    override fun sendSms(body: List<SmsDevBody>): Flow<List<SmsDevEntity>> {
        return flow {
            emit(repository.sendSms(body))
        }.flowOn(coroutineDispatcher)
    }
}