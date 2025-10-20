package br.com.lucolimac.shesafe.android.domain.repository.api

import br.com.lucolimac.shesafe.android.domain.entity.InfoBipBody
import br.com.lucolimac.shesafe.android.domain.entity.InfoBipEntity
import br.com.lucolimac.shesafe.android.domain.entity.InfoBipFailure
import br.com.lucolimac.shesafe.android.domain.entity.SheSafeResult

interface InfoBipRepository {
    suspend fun sendSms(
        body: InfoBipBody, authorization: String
    ): SheSafeResult<InfoBipEntity, InfoBipFailure>
}