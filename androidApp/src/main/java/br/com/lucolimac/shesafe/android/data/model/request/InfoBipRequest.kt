package br.com.lucolimac.shesafe.android.data.model.request

import br.com.lucolimac.shesafe.android.domain.entity.InfoBipBody
import com.google.gson.annotations.SerializedName

data class InfoBipRequest(
    @SerializedName("messages") val messages: List<Message>
) {
    data class Message(
        @SerializedName("content") val content: Content,
        @SerializedName("destinations") val destinations: List<Destination>,
    ) {
        data class Destination(
            @SerializedName("to") val to: String
        ) {
            companion object {
                fun fromEntity(body: InfoBipBody.Message.Destination) = Destination(
                    to = body.to
                )
            }
        }

        data class Content(
            @SerializedName("text") val text: String
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
