package br.com.lucolimac.shesafe.android.data.source

import br.com.lucolimac.shesafe.android.data.model.HelpRequestModel

interface HelpRequestDataSource {
    suspend fun getHelpRequests(): List<HelpRequestModel>

    suspend fun registerHelpRequest(contact: HelpRequestModel): Boolean
}
