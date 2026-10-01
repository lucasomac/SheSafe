package br.com.lucolimac.shesafe.data.model

import br.com.lucolimac.shesafe.domain.entity.InfoBipBody
import br.com.lucolimac.shesafe.domain.entity.InfoBipEntity
import br.com.lucolimac.shesafe.domain.entity.InfoBipFailure
import kotlinx.serialization.Serializable

@Serializable
data class InfoBipRequest(
    val messages: List<Message>
) {
    @Serializable
    data class Message(
        val content: Content,
        val destinations: List<Destination>,
    ) {
        @Serializable
        data class Destination(
            val to: String
        ) {
            companion object {
                fun fromEntity(body: InfoBipBody.Message.Destination) = Destination(
                    to = body.to
                )
            }
        }

        @Serializable
        data class Content(
            val text: String
        ) {
            companion object {
                fun fromEntity(body: InfoBipBody.Message.Content) = Content(
                    text = body.text
                )
            }
        }

        companion object {
            fun fromEntity(body: InfoBipBody.Message) = Message(
                content = Content.fromEntity(body.content),
                destinations = body.destinations.map { Destination.fromEntity(it) },
            )
        }
    }

    companion object {
        fun fromEntity(body: InfoBipBody) = InfoBipRequest(
            messages = body.messages.map { Message.fromEntity(it) })
    }
}

@Serializable
data class InfoBipResponse(
    val bulkId: String,
    val messages: List<Message>
) {
    @Serializable
    data class Message(
        val destination: String,
        val details: Details?,
        val messageId: String,
        val status: Status
    ) {
        @Serializable
        data class Details(
            val messageCount: Int
        ) {
            fun toEntity() = InfoBipEntity.Message.Details(messageCount = messageCount)
        }

        @Serializable
        data class Status(
            val description: String,
            val groupId: Int,
            val groupName: String,
            val id: Int,
            val name: String
        ) {
            fun toEntity() = InfoBipEntity.Message.Status(
                description = description,
                groupId = groupId,
                groupName = groupName,
                id = id,
                name = name
            )
        }

        fun toEntity() = InfoBipEntity.Message(
            destination = destination,
            details = details?.toEntity(),
            messageId = messageId,
            status = status.toEntity()
        )
    }

    fun toEntity() = InfoBipEntity(bulkId = bulkId, messages = messages.map { it.toEntity() })
}

@Serializable
data class InfoBipFailureResponse(
    val action: String,
    val description: String,
    val errorCode: String,
    val resources: List<Resource>,
    val violations: List<Violation>
) {
    fun toEntity(): InfoBipFailure {
        return InfoBipFailure(
            this.action,
            this.description,
            this.errorCode,
            this.resources.map { it.toEntity() },
            this.violations.map { it.toEntity() })
    }

    @Serializable
    data class Resource(
        val name: String, val url: String
    ) {
        fun toEntity(): InfoBipFailure.Resource {
            return InfoBipFailure.Resource(this.name, this.url)
        }
    }

    @Serializable
    data class Violation(
        val property: String,
        val violation: String
    ) {
        fun toEntity(): InfoBipFailure.Violation {
            return InfoBipFailure.Violation(this.property, this.violation)
        }
    }
}
