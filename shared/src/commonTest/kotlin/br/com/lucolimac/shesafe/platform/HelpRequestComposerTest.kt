package br.com.lucolimac.shesafe.platform

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Unit tests verifying emergency message formatting in [HelpRequestComposer].
 */
class HelpRequestComposerTest {
    /**
     * Verifies that [HelpRequestComposer.composeMessage] trims the base message and appends a
     * Google Maps search URL when [LocationCoordinates] are provided.
     */
    @Test
    fun appendsGoogleMapsLinkWhenLocationIsAvailable() {
        assertEquals(
            "Help me https://www.google.com/maps/search/?api=1&query=-23.55,-46.63",
            HelpRequestComposer.composeMessage(
                message = "  Help me ",
                location = LocationCoordinates(-23.55, -46.63),
            ),
        )
    }

    /**
     * Verifies that [HelpRequestComposer.composeMessage] returns only the trimmed message text
     * when location coordinates are `null`.
     */
    @Test
    fun leavesMessageWithoutLocationLinkWhenLocationIsUnavailable() {
        assertEquals("Help me", HelpRequestComposer.composeMessage(" Help me ", null))
    }
}
