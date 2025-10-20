package br.com.lucolimac.shesafe.android.domain.entity


data class InfoBipEntity(
    val bulkId: String, val messages: List<Message>
) {
    data class Message(
        val destination: String, val details: Details?, val messageId: String, val status: Status
    ) {
        data class Details(
            val messageCount: Int
        )
        data class Status( val description: String,val groupId: Int, val groupName: String, val id: Int,val name: String
        )
    }
}