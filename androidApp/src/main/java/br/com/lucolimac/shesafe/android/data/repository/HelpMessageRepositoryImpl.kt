package br.com.lucolimac.shesafe.android.data.repository

import br.com.lucolimac.shesafe.android.data.source.HelpMessageDataSource
import br.com.lucolimac.shesafe.android.domain.repository.HelpMessageRepository

class HelpMessageRepositoryImpl(private val dataSource: HelpMessageDataSource) :
    HelpMessageRepository {
    override suspend fun getHelpMessages(): String {
        return try {
            dataSource.getHelpMessage()
        } catch (e: Exception) {
            // Handle the exception, e.g., log it or return a default value
            e.printStackTrace()
            ""
        }
    }

    override suspend fun registerHelpMessage(message: String): Boolean {
        return try {
            dataSource.registerHelpMessage(message)
        } catch (e: Exception) {
            // Handle the exception, e.g., log it or return a default value
            false // Default value in case of an error
        }
    }
}