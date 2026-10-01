package br.com.lucolimac.shesafe.android.framework.data.source.api

import br.com.lucolimac.shesafe.android.data.source.api.SmsDevDataSource
import br.com.lucolimac.shesafe.data.model.SmsDevItemRequest
import br.com.lucolimac.shesafe.data.model.SmsDevItemResponse
import br.com.lucolimac.shesafe.framework.service.api.SmsDevService
import de.jensklingenberg.ktorfit.Ktorfit

class SmsDevDataSourceImpl(private val ktorfit: Ktorfit) : SmsDevDataSource {
    private val service by lazy { ktorfit.create<SmsDevService>() }

    override suspend fun sendSms(request: List<SmsDevItemRequest>): List<SmsDevItemResponse> {
        return service.sendSms(request)
    }
}
