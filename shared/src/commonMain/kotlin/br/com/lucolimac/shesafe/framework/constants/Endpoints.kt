package br.com.lucolimac.shesafe.framework.constants

/**
 * Relative HTTP endpoint paths used by Ktorfit SMS gateway service interfaces.
 */
object Endpoints {
    /**
     * Relative endpoint path for dispatching advanced text SMS messages via the Infobip REST API.
     */
    const val INFO_BIP_SEND_SMS = "sms/2/text/advanced"

    /**
     * Relative endpoint path for dispatching SMS batches via the SMSDev v1 REST API.
     */
    const val SMS_DEV_SEND_SMS = "v1/send"
}
