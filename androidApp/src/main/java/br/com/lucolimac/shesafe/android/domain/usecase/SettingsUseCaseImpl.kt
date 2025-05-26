package br.com.lucolimac.shesafe.android.domain.usecase

import br.com.lucolimac.shesafe.android.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class SettingsUseCaseImpl(private val settingsRepository: SettingsRepository) : SettingsUseCase {
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
    override fun getToggleSetting(settingsEnum: String): Flow<Boolean> {
        return flow {
            emit(settingsRepository.getToggleSetting(settingsEnum))
        }.flowOn(coroutineDispatcher)
    }

    override fun setToggleSetting(
        settingsEnum: String, value: Boolean
    ): Flow<Boolean> {
        return flow {
            emit(settingsRepository.setToggleSetting(settingsEnum, value))
        }.flowOn(coroutineDispatcher)
    }
}