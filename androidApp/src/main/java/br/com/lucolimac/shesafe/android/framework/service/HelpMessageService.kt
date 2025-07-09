package br.com.lucolimac.shesafe.android.framework.service

interface HelpMessageService {
    suspend fun getHelpMessage(): String
    suspend fun registerHelpMessage(message: String): Boolean
}