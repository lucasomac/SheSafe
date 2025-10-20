package br.com.lucolimac.shesafe.android.data.model.response


import br.com.lucolimac.shesafe.android.domain.entity.InfoBipEntity
import com.google.gson.annotations.SerializedName

data class InfoBipResponse(
    @SerializedName("bulkId") val bulkId: String,
    @SerializedName("messages") val messages: List<Message>
) {
    data class Message(
        @SerializedName("destination") val destination: String,
        @SerializedName("details") val details: Details?,
        @SerializedName("messageId") val messageId: String,
        @SerializedName("status") val status: Status
    ) {
        data class Details(
            @SerializedName("messageCount") val messageCount: Int
        ) {
            fun toEntity() = InfoBipEntity.Message.Details(messageCount = messageCount)
        }

        data class Status(
            @SerializedName("description") val description: String,
            @SerializedName("groupId") val groupId: Int,
            @SerializedName("groupName") val groupName: String,
            @SerializedName("id") val id: Int,
            @SerializedName("name") val name: String
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