package br.com.lucolimac.shesafe.android.data.repository.api

import android.util.Log
import br.com.lucolimac.shesafe.android.data.model.request.SmsDevItemRequest
import br.com.lucolimac.shesafe.android.data.model.response.SmsDevItemResponse
import br.com.lucolimac.shesafe.android.data.source.api.SmsDevDataSource
import br.com.lucolimac.shesafe.android.domain.entity.SmsDevBody
import br.com.lucolimac.shesafe.android.domain.entity.SmsDevEntity
import br.com.lucolimac.shesafe.android.domain.repository.api.SmsDevRepository

class SmsDevRepositoryImpl(private val dataSource: SmsDevDataSource) : SmsDevRepository {
    override suspend fun sendSms(body: List<SmsDevBody>): List<SmsDevEntity> {
        return try {
            val response = dataSource.sendSms(body.map { SmsDevItemRequest.Companion.fromEntity(it) })
            if (response.isSuccessful) {
                val smsResponse: List<SmsDevItemResponse>? = response.body()
                smsResponse?.map { it.toEntity() } ?: emptyList()
            } else {
                emptyList()
            }
        } catch (ex: Exception) {
            Log.e("SmsDevRepositoryImpl", "sendSms: ", ex)
            emptyList()
        }
    }
}