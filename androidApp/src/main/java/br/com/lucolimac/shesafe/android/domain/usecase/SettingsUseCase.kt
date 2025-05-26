package br.com.lucolimac.shesafe.android.domain.usecase

import kotlinx.coroutines.flow.Flow

interface SettingsUseCase {
    fun getToggleSetting(settingsEnum: String): Flow<Boolean>
    fun setToggleSetting(settingsEnum: String, value: Boolean): Flow<Boolean>
}