package br.com.lucolimac.shesafe.android.framework.constants

object Endpoints {
    object Version{

    }
    private const val VERSION_SMS_DEV_API = "/v1"
    const val SMS_DEV_HOST = "https://api.smsdev.com.br"
    const val INFO_BIP_HOST = "https://ypw5k1.api.infobip.com"
    const val SMS_DEV_SEND_SMS = "$VERSION_SMS_DEV_API/send"
    const val INFO_BIP_SEND_SMS = "/sms/3/messages"
}