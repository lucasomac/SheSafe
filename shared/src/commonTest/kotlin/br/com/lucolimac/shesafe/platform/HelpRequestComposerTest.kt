package br.com.lucolimac.shesafe.platform

import kotlin.test.Test
import kotlin.test.assertEquals

class HelpRequestComposerTest {
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

    @Test
    fun leavesMessageWithoutLocationLinkWhenLocationIsUnavailable() {
        assertEquals("Help me", HelpRequestComposer.composeMessage(" Help me ", null))
    }
}
