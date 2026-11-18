package br.com.lucolimac.shesafe.domain.entity

/**
 * Domain entity representing a successful SMS dispatch response from the Infobip gateway.
 *
 * @property bulkId Identifier assigned to a multi-message bulk submission, if present.
 * @property messages Per-recipient [Message] delivery status entries returned by Infobip.
 */
data class InfoBipEntity(
    val bulkId: String, val messages: List<Message>
) {
    /**
     * Domain representation of the dispatch status for a single recipient destination.
     *
     * @property destination Phone number to which the SMS was sent.
     * @property details Optional SMS segmentation metadata, or `null` when omitted by the gateway.
     * @property messageId Unique identifier assigned by Infobip to this message.
     * @property status Delivery acceptance [Status] reported for this message.
     */
    data class Message(
        val destination: String, val details: Details?, val messageId: String, val status: Status
    ) {
        /**
         * Domain metadata describing how an SMS body was segmented for transmission.
         *
         * @property messageCount Number of SMS segments used for the message.
         */
        data class Details(
            val messageCount: Int
        )

        /**
         * Domain representation of an Infobip message delivery status code and category.
         *
         * @property description Human-readable explanation of the status.
         * @property groupId Numeric identifier of the status group.
         * @property groupName Symbolic name of the status group (e.g., `PENDING`, `REJECTED`).
         * @property id Numeric identifier of the specific status code.
         * @property name Symbolic name of the specific status code.
         */
        data class Status( val description: String,val groupId: Int, val groupName: String, val id: Int,val name: String
        )
    }
}
