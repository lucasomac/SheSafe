package br.com.lucolimac.shesafe.platform

object HelpRequestComposer {
    fun composeMessage(
        message: String,
        location: LocationCoordinates?,
    ): String {
        val locationSuffix = location?.let {
            " https://www.google.com/maps/search/?api=1&query=${it.latitude},${it.longitude}"
        }.orEmpty()
        return message.trim() + locationSuffix
    }
}
