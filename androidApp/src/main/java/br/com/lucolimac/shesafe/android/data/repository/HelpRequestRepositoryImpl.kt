package br.com.lucolimac.shesafe.android.data.repository

import br.com.lucolimac.shesafe.android.data.model.HelpRequestModel
import br.com.lucolimac.shesafe.android.data.source.HelpRequestDataSource
import br.com.lucolimac.shesafe.android.domain.entity.HelpRequest
import br.com.lucolimac.shesafe.android.domain.repository.HelpRequestRepository

class HelpRequestRepositoryImpl(
    private val helpRequestDataSource: HelpRequestDataSource
) : HelpRequestRepository {
    override suspend fun getHelpRequests(): List<HelpRequest> {
        return helpRequestDataSource.getHelpRequests().map {
            HelpRequest(
                phone = it.phone, location = it.location, createdAt = it.createdAt
            )
        }
    }

    override suspend fun registerHelpRequest(helpRequest: HelpRequest): Boolean {
        return helpRequestDataSource.registerHelpRequest(
            HelpRequestModel.fromEntity(helpRequest))
    }
}