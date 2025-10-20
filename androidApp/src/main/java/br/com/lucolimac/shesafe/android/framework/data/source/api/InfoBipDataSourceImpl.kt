package br.com.lucolimac.shesafe.android.framework.data.source.api

import br.com.lucolimac.shesafe.android.data.model.request.InfoBipRequest
import br.com.lucolimac.shesafe.android.data.model.response.InfoBipResponse
import br.com.lucolimac.shesafe.android.data.source.api.InfoBipDataSource
import br.com.lucolimac.shesafe.android.framework.service.api.InfoBipService
import retrofit2.Response
import retrofit2.Retrofit

class InfoBipDataSourceImpl(private val retrofit: Retrofit) : InfoBipDataSource {
    private val service by lazy { retrofit.create(InfoBipService::class.java) }
    override suspend fun sendSms(
        request: InfoBipRequest, authorization: String
    ): Response<InfoBipResponse> {
        return service.sendSms(authorization, request)
    }
}