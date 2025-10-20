package br.com.lucolimac.shesafe.android.domain.entity

data class InfoBipBody(
    val messages: List<Message>
) {
    data class Message(
        val content: Content, val destinations: List<Destination>, val sender: String
    ) {
        data class Destination(
            val to: String
        )

        data class Content(
            val text: String
        )
    }
}