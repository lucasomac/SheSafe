package br.com.lucolimac.shesafe.android.framework.service

import br.com.lucolimac.shesafe.android.data.model.HelpRequestModel

interface HelpRequestService {
    suspend fun getOrdersHelp(): List<HelpRequestModel>
    suspend fun registerOrderHelp(
        helpRequestModel: HelpRequestModel
    ): Boolean
}