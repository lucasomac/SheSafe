package br.com.lucolimac.shesafe.android.framework.data.source

import br.com.lucolimac.shesafe.android.data.model.HelpRequestModel
import br.com.lucolimac.shesafe.android.data.source.HelpRequestDataSource
import br.com.lucolimac.shesafe.android.framework.service.HelpRequestService

class HelpRequestDataSourceImpl(private val helpRequestService: HelpRequestService) :
    HelpRequestDataSource {
    override suspend fun getHelpRequests(): List<HelpRequestModel> {
        return helpRequestService.getOrdersHelp()
    }

    override suspend fun registerHelpRequest(contact: HelpRequestModel): Boolean {
        return helpRequestService.registerOrderHelp(contact)
    }
}