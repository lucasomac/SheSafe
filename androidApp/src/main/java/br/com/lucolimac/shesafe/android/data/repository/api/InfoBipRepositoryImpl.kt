package br.com.lucolimac.shesafe.android.data.repository.api

import android.util.Log
import br.com.lucolimac.shesafe.android.data.model.request.InfoBipRequest
import br.com.lucolimac.shesafe.android.data.model.response.InfoBipFailureResponse
import br.com.lucolimac.shesafe.android.data.model.response.InfoBipResponse
import br.com.lucolimac.shesafe.android.data.source.api.InfoBipDataSource
import br.com.lucolimac.shesafe.android.domain.entity.InfoBipBody
import br.com.lucolimac.shesafe.android.domain.entity.InfoBipEntity
import br.com.lucolimac.shesafe.android.domain.entity.InfoBipFailure
import br.com.lucolimac.shesafe.android.domain.entity.SheSafeResult
import br.com.lucolimac.shesafe.android.domain.repository.api.InfoBipRepository
import com.google.gson.Gson

class InfoBipRepositoryImpl(private val dataSource: InfoBipDataSource) : InfoBipRepository {
    override suspend fun sendSms(
        body: InfoBipBody, authorization: String
    ): SheSafeResult<InfoBipEntity, InfoBipFailure> {
        return try {
            val response = dataSource.sendSms(InfoBipRequest.fromEntity(body), authorization)
            if (response.isSuccessful) {
                val smsResponse: InfoBipResponse = response.body() ?: return SheSafeResult.Error()
                SheSafeResult.Success(smsResponse.toEntity())
            } else {
                val responseError = response.errorBody()?.string()
                val error = Gson().fromJson(responseError, InfoBipFailureResponse::class.java)
                SheSafeResult.Failure(error.toEntity())
            }
        } catch (ex: Exception) {
            Log.e("SmsDevRepositoryImpl", "sendSms: ", ex)
            SheSafeResult.Error(ex)
        }
    }
}