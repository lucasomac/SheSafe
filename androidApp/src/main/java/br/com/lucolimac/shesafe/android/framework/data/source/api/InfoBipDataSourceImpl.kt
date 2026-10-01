package br.com.lucolimac.shesafe.android.framework.data.source.api

import br.com.lucolimac.shesafe.android.data.source.api.InfoBipDataSource
import br.com.lucolimac.shesafe.data.model.InfoBipRequest
import br.com.lucolimac.shesafe.data.model.InfoBipResponse
import br.com.lucolimac.shesafe.framework.service.api.InfoBipService
import br.com.lucolimac.shesafe.framework.service.api.SmsDevService
import de.jensklingenberg.ktorfit.Ktorfit
import kotlin.getValue

class InfoBipDataSourceImpl(private val ktorfit: Ktorfit) : InfoBipDataSource {
    private val service by lazy { ktorfit.create<InfoBipService>() }
    override suspend fun sendSms(
        request: InfoBipRequest, authorization: String
    ): InfoBipResponse {
        return service.sendSms(authorization, request)
    }
}
