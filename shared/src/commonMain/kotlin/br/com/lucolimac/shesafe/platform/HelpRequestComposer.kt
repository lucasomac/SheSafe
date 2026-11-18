package br.com.lucolimac.shesafe.platform

/**
 * Utility responsible for assembling the outbound emergency SMS body text.
 */
object HelpRequestComposer {
    /**
     * Trims [message] and appends a Google Maps search URL when [location] coordinates are provided.
     *
     * @param message User-configured or default emergency SOS text.
     * @param location Resolved geographic coordinates to embed as a map link, or `null` if unavailable.
     * @return Composed SMS payload ready for transmission.
     */
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
