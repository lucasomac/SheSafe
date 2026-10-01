package br.com.lucolimac.shesafe.android.domain.usecase.api

import br.com.lucolimac.shesafe.domain.entity.InfoBipBody
import br.com.lucolimac.shesafe.domain.entity.InfoBipEntity
import br.com.lucolimac.shesafe.domain.entity.InfoBipFailure
import br.com.lucolimac.shesafe.android.domain.entity.SheSafeResult
import kotlinx.coroutines.flow.Flow

interface InfoBipUseCase {
    fun sendSms(
        body: InfoBipBody, authorization: String
    ): Flow<SheSafeResult<InfoBipEntity, InfoBipFailure>>
}