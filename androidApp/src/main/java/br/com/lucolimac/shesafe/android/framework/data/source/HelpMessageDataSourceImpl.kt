package br.com.lucolimac.shesafe.android.framework.data.source

import br.com.lucolimac.shesafe.android.data.source.HelpMessageDataSource
import br.com.lucolimac.shesafe.android.framework.service.HelpMessageService

class HelpMessageDataSourceImpl(private val helpMessageService: HelpMessageService) :
    HelpMessageDataSource {
    override suspend fun getHelpMessage(): String {
        return helpMessageService.getHelpMessage()
    }

    override suspend fun registerHelpMessage(message: String): Boolean {
        return try {
            helpMessageService.registerHelpMessage(message)
            true
        } catch (e: Exception) {
            false
        }
    }
}