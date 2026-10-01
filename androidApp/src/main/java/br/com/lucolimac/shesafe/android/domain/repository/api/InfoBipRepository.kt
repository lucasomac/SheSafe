package br.com.lucolimac.shesafe.android.domain.repository.api

import br.com.lucolimac.shesafe.android.domain.entity.SheSafeResult
import br.com.lucolimac.shesafe.domain.entity.InfoBipBody
import br.com.lucolimac.shesafe.domain.entity.InfoBipEntity
import br.com.lucolimac.shesafe.domain.entity.InfoBipFailure

interface InfoBipRepository {
    suspend fun sendSms(
        body: InfoBipBody, authorization: String
    ): SheSafeResult<InfoBipEntity, InfoBipFailure>
}