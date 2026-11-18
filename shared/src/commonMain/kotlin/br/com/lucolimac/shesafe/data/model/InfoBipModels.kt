package br.com.lucolimac.shesafe.data.model

import br.com.lucolimac.shesafe.domain.entity.InfoBipBody
import br.com.lucolimac.shesafe.domain.entity.InfoBipEntity
import br.com.lucolimac.shesafe.domain.entity.InfoBipFailure
import kotlinx.serialization.Serializable

/**
 * Serializable request payload sent to the Infobip advanced SMS API endpoint.
 *
 * @property messages Batch of outbound SMS [Message] definitions to dispatch.
 */
@Serializable
data class InfoBipRequest(
    val messages: List<Message>
) {
    /**
     * Single outbound SMS message definition within an [InfoBipRequest].
     *
     * @property content Text body wrapper for the SMS message.
     * @property destinations Recipient phone number targets for this message.
     */
    @Serializable
    data class Message(
        val content: Content,
        val destinations: List<Destination>,
    ) {
        /**
         * Recipient destination entry for an Infobip SMS message.
         *
         * @property to E.164 or country-prefixed destination phone number.
         */
        @Serializable
        data class Destination(
            val to: String
        ) {
            /**
             * Factory methods for creating [Destination] DTOs from domain models.
             */
            companion object {
                /**
                 * Converts a domain [InfoBipBody.Message.Destination] into a serializable [Destination].
                 *
                 * @param body Domain destination entity to map.
                 * @return Mapped [Destination] network DTO.
                 */
                fun fromEntity(body: InfoBipBody.Message.Destination) = Destination(
                    to = body.to
                )
            }
        }

        /**
         * Text content payload of an Infobip SMS message.
         *
         * @property text Plain-text SMS body to transmit.
         */
        @Serializable
        data class Content(
            val text: String
        ) {
            /**
             * Factory methods for creating [Content] DTOs from domain models.
             */
            companion object {
                /**
                 * Converts a domain [InfoBipBody.Message.Content] into a serializable [Content].
                 *
                 * @param body Domain content entity to map.
                 * @return Mapped [Content] network DTO.
                 */
                fun fromEntity(body: InfoBipBody.Message.Content) = Content(
                    text = body.text
                )
            }
        }

        /**
         * Factory methods for creating [Message] DTOs from domain models.
         */
        companion object {
            /**
             * Converts a domain [InfoBipBody.Message] into a serializable [Message].
             *
             * @param body Domain message entity to map.
             * @return Mapped [Message] network DTO.
             */
            fun fromEntity(body: InfoBipBody.Message) = Message(
                content = Content.fromEntity(body.content),
                destinations = body.destinations.map { Destination.fromEntity(it) },
            )
        }
    }

    /**
     * Factory methods for creating [InfoBipRequest] DTOs from domain models.
     */
    companion object {
        /**
         * Converts a domain [InfoBipBody] into a serializable [InfoBipRequest].
         *
         * @param body Domain request body containing outbound SMS messages.
         * @return Mapped [InfoBipRequest] network DTO.
         */
        fun fromEntity(body: InfoBipBody) = InfoBipRequest(
            messages = body.messages.map { Message.fromEntity(it) })
    }
}

/**
 * Serializable success response returned by the Infobip advanced SMS API.
 *
 * @property bulkId Identifier assigned to multi-destination bulk dispatches, if applicable.
 * @property messages Per-destination delivery acceptance results.
 */
@Serializable
data class InfoBipResponse(
    val bulkId: String = "",
    val messages: List<Message> = emptyList(),
) {
    /**
     * Per-recipient dispatch status entry within an [InfoBipResponse].
     *
     * @property destination Phone number to which the SMS was addressed.
     * @property details Optional segmentation metadata for the dispatched SMS.
     * @property messageId Unique Infobip identifier assigned to this individual SMS.
     * @property status Delivery acceptance status reported by Infobip.
     */
    @Serializable
    data class Message(
        val destination: String = "",
        val details: Details? = null,
        val messageId: String = "",
        val status: Status,
    ) {
        /**
         * Additional SMS segmentation details returned for an accepted message.
         *
         * @property messageCount Number of SMS segments used to encode the message text.
         */
        @Serializable
        data class Details(
            val messageCount: Int
        ) {
            /**
             * Maps this [Details] DTO to its domain [InfoBipEntity.Message.Details] counterpart.
             *
             * @return Corresponding domain [InfoBipEntity.Message.Details] instance.
             */
            fun toEntity() = InfoBipEntity.Message.Details(messageCount = messageCount)
        }

        /**
         * Structured status code and group metadata returned by Infobip for a message.
         *
         * @property description Human-readable explanation of the delivery status.
         * @property groupId Numeric identifier of the status category (e.g., pending, rejected).
         * @property groupName Symbolic name of the status category.
         * @property id Specific Infobip status code identifier.
         * @property name Symbolic name of the specific status code.
         */
        @Serializable
        data class Status(
            val description: String,
            val groupId: Int,
            val groupName: String,
            val id: Int,
            val name: String
        ) {
            /**
             * Maps this [Status] DTO to its domain [InfoBipEntity.Message.Status] counterpart.
             *
             * @return Corresponding domain [InfoBipEntity.Message.Status] instance.
             */
            fun toEntity() = InfoBipEntity.Message.Status(
                description = description,
                groupId = groupId,
                groupName = groupName,
                id = id,
                name = name
            )
        }

        /**
         * Maps this [Message] DTO to its domain [InfoBipEntity.Message] counterpart.
         *
         * @return Corresponding domain [InfoBipEntity.Message] instance.
         */
        fun toEntity() = InfoBipEntity.Message(
            destination = destination,
            details = details?.toEntity(),
            messageId = messageId,
            status = status.toEntity()
        )
    }

    /**
     * Maps this [InfoBipResponse] DTO to its domain [InfoBipEntity] counterpart.
     *
     * @return Corresponding domain [InfoBipEntity] instance.
     */
    fun toEntity() = InfoBipEntity(bulkId = bulkId, messages = messages.map { it.toEntity() })
}

/**
 * Serializable error payload returned by the Infobip API when a request fails validation or execution.
 *
 * @property action Recommended corrective action provided by Infobip.
 * @property description Human-readable summary of the API error.
 * @property errorCode Machine-readable Infobip error code.
 * @property resources Documentation links relevant to resolving the error.
 * @property violations Specific request property validation violations, if any.
 */
@Serializable
data class InfoBipFailureResponse(
    val action: String,
    val description: String,
    val errorCode: String,
    val resources: List<Resource>,
    val violations: List<Violation>
) {
    /**
     * Maps this [InfoBipFailureResponse] DTO to its domain [InfoBipFailure] counterpart.
     *
     * @return Corresponding domain [InfoBipFailure] instance.
     */
    fun toEntity(): InfoBipFailure {
        return InfoBipFailure(
            this.action,
            this.description,
            this.errorCode,
            this.resources.map { it.toEntity() },
            this.violations.map { it.toEntity() })
    }

    /**
     * External documentation resource referenced in an [InfoBipFailureResponse].
     *
     * @property name Display title of the documentation resource.
     * @property url URL pointing to the Infobip documentation page.
     */
    @Serializable
    data class Resource(
        val name: String, val url: String
    ) {
        /**
         * Maps this [Resource] DTO to its domain [InfoBipFailure.Resource] counterpart.
         *
         * @return Corresponding domain [InfoBipFailure.Resource] instance.
         */
        fun toEntity(): InfoBipFailure.Resource {
            return InfoBipFailure.Resource(this.name, this.url)
        }
    }

    /**
     * Field-level validation violation reported within an [InfoBipFailureResponse].
     *
     * @property property JSON path or field name that violated validation constraints.
     * @property violation Description of the violated constraint.
     */
    @Serializable
    data class Violation(
        val property: String,
        val violation: String
    ) {
        /**
         * Maps this [Violation] DTO to its domain [InfoBipFailure.Violation] counterpart.
         *
         * @return Corresponding domain [InfoBipFailure.Violation] instance.
         */
        fun toEntity(): InfoBipFailure.Violation {
            return InfoBipFailure.Violation(this.property, this.violation)
        }
    }
}
