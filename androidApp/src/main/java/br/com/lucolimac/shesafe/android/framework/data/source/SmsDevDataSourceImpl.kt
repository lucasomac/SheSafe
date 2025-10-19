package br.com.lucolimac.shesafe.android.framework.data.source

import br.com.lucolimac.shesafe.android.data.model.request.SmsDevItemRequest
import br.com.lucolimac.shesafe.android.data.model.response.SmsDevItemResponse
import br.com.lucolimac.shesafe.android.data.source.SmsDevDataSource
import br.com.lucolimac.shesafe.android.framework.service.SmsDevService
import retrofit2.Response
import retrofit2.Retrofit

class SmsDevDataSourceImpl(private val retrofit: Retrofit) : SmsDevDataSource {
    private val service by lazy { retrofit.create(SmsDevService::class.java) }

    override suspend fun sendSms(request: List<SmsDevItemRequest>): Response<List<SmsDevItemResponse>> {
        return service.sendSms(request)
    }
}