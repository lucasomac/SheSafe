package br.com.lucolimac.shesafe.enum

enum class SettingsEnum(val label: String, var isEnabled: Boolean) {
    SEND_CONFIRMATION("Confirmação de envio", true), NOTIFICATION("Notificações", true);

    fun changeToggleSetting() {
        isEnabled = !isEnabled
    }
}