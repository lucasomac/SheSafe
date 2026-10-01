package br.com.lucolimac.shesafe.android.data.repository.api

import android.util.Log
import br.com.lucolimac.shesafe.data.model.InfoBipRequest
import br.com.lucolimac.shesafe.android.data.source.api.InfoBipDataSource
import br.com.lucolimac.shesafe.domain.entity.InfoBipBody
import br.com.lucolimac.shesafe.domain.entity.InfoBipEntity
import br.com.lucolimac.shesafe.domain.entity.InfoBipFailure
import br.com.lucolimac.shesafe.android.domain.entity.SheSafeResult
import br.com.lucolimac.shesafe.android.domain.repository.api.InfoBipRepository
import br.com.lucolimac.shesafe.data.model.InfoBipFailureResponse
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.call.body

class InfoBipRepositoryImpl(private val dataSource: InfoBipDataSource) : InfoBipRepository {
    override suspend fun sendSms(
        body: InfoBipBody, authorization: String
    ): SheSafeResult<InfoBipEntity, InfoBipFailure> {
        return try {
            val response = dataSource.sendSms(InfoBipRequest.fromEntity(body), authorization)
            SheSafeResult.Success(response.toEntity())
        } catch (ex: ClientRequestException) {
            try {
                val errorBody = ex.response.body<InfoBipFailureResponse>()
                SheSafeResult.Failure(errorBody.toEntity())
            } catch (inner: Exception) {
                SheSafeResult.Error(ex)
            }
        } catch (ex: Exception) {
            Log.e("InfoBipRepositoryImpl", "sendSms: ", ex)
            SheSafeResult.Error(ex)
        }
    }
}
