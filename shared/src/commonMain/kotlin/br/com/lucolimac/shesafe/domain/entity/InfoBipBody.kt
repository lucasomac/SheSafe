package br.com.lucolimac.shesafe.domain.entity

/**
 * Domain model representing an outbound batch SMS request body for the Infobip gateway.
 *
 * @property messages Collection of outbound SMS [Message] items to be dispatched.
 */
data class InfoBipBody(
    val messages: List<Message>
) {
    /**
     * Domain representation of an individual SMS message and its recipient targets.
     *
     * @property content Text content of the SMS message.
     * @property destinations List of recipient [Destination] entries receiving [content].
     */
    data class Message(
        val content: Content, val destinations: List<Destination>
    ) {
        /**
         * Domain representation of a single SMS recipient destination.
         *
         * @property to Recipient phone number in international format.
         */
        data class Destination(
            val to: String
        )

        /**
         * Domain representation of the text body of an outbound SMS.
         *
         * @property text Plain-text SMS content.
         */
        data class Content(
            val text: String
        )
    }
}
