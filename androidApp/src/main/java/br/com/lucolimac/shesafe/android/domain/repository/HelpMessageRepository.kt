package br.com.lucolimac.shesafe.android.domain.repository

interface HelpMessageRepository {
  suspend  fun getHelpMessages(): String
  suspend  fun registerHelpMessage(message: String): Boolean
}