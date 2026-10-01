package br.com.lucolimac.shesafe.android.data.repository.api

import android.util.Log
import br.com.lucolimac.shesafe.data.model.SmsDevItemRequest
import br.com.lucolimac.shesafe.android.data.source.api.SmsDevDataSource
import br.com.lucolimac.shesafe.domain.entity.SmsDevBody
import br.com.lucolimac.shesafe.domain.entity.SmsDevEntity
import br.com.lucolimac.shesafe.android.domain.repository.api.SmsDevRepository

class SmsDevRepositoryImpl(private val dataSource: SmsDevDataSource) : SmsDevRepository {
    override suspend fun sendSms(body: List<SmsDevBody>): List<SmsDevEntity> {
        return try {
            val response = dataSource.sendSms(body.map { SmsDevItemRequest.fromEntity(it) })
            response.map { it.toEntity() }
        } catch (ex: Exception) {
            Log.e("SmsDevRepositoryImpl", "sendSms: ", ex)
            emptyList()
        }
    }
}
