package br.com.lucolimac.shesafe.android.domain.usecase.api

import br.com.lucolimac.shesafe.android.domain.entity.InfoBipBody
import br.com.lucolimac.shesafe.android.domain.entity.InfoBipEntity
import br.com.lucolimac.shesafe.android.domain.entity.InfoBipFailure
import br.com.lucolimac.shesafe.android.domain.entity.SheSafeResult
import br.com.lucolimac.shesafe.android.domain.entity.SmsDevBody
import br.com.lucolimac.shesafe.android.domain.entity.SmsDevEntity
import br.com.lucolimac.shesafe.android.domain.repository.api.InfoBipRepository
import br.com.lucolimac.shesafe.android.domain.repository.api.SmsDevRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class InfoBipUseCaseImpl(
    private val repository: InfoBipRepository
) : InfoBipUseCase {
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO

    override fun sendSms(
        body: InfoBipBody, authorization: String
    ): Flow<SheSafeResult<InfoBipEntity, InfoBipFailure>> {
        return flow {
            emit(repository.sendSms(body, authorization))
        }.flowOn(coroutineDispatcher)
    }
}