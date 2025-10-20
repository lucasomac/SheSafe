package br.com.lucolimac.shesafe.android.domain.usecase.api

import br.com.lucolimac.shesafe.android.domain.entity.InfoBipBody
import br.com.lucolimac.shesafe.android.domain.entity.InfoBipEntity
import br.com.lucolimac.shesafe.android.domain.entity.InfoBipFailure
import br.com.lucolimac.shesafe.android.domain.entity.SheSafeResult
import br.com.lucolimac.shesafe.android.domain.entity.SmsDevBody
import br.com.lucolimac.shesafe.android.domain.entity.SmsDevEntity
import kotlinx.coroutines.flow.Flow

interface InfoBipUseCase {
    fun sendSms(
        body: InfoBipBody, authorization: String
    ): Flow<SheSafeResult<InfoBipEntity, InfoBipFailure>>
}