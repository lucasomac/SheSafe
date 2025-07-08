package br.com.lucolimac.shesafe.android.data.source

interface HelpMessageDataSource {
    suspend fun getHelpMessage(): String
    suspend fun registerHelpMessage(message: String): Boolean
}