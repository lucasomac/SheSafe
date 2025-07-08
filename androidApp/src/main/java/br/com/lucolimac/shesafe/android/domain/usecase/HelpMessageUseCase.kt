package br.com.lucolimac.shesafe.android.domain.usecase

import kotlinx.coroutines.flow.Flow

interface HelpMessageUseCase {
    suspend fun getHelpMessages(): Flow<String>

    suspend fun registerHelpMessage(message: String): Flow<Boolean>
}