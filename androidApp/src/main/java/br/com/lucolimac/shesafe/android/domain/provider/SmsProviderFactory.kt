package br.com.lucolimac.shesafe.android.domain.provider

import br.com.lucolimac.shesafe.android.domain.usecase.api.InfoBipUseCase
import br.com.lucolimac.shesafe.android.domain.usecase.api.SmsDevUseCase

enum class SmsProviderType {
    SMS_DEV,
    INFO_BIP
}

class SmsProviderFactory(
    private val smsDevUseCase: SmsDevUseCase,
    private val infoBipUseCase: InfoBipUseCase,
    private val smsDevApiKey: String,
    private val infoBipApiKey: String
) {
    fun getProvider(type: SmsProviderType): SmsProvider {
        return when (type) {
            SmsProviderType.SMS_DEV -> SmsDevProvider(smsDevUseCase, smsDevApiKey)
            SmsProviderType.INFO_BIP -> InfoBipProvider(infoBipUseCase, infoBipApiKey)
        }
    }
}
