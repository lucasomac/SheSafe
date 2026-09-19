package br.com.lucolimac.shesafe

import kotlin.test.Test
import br.com.lucolimac.shesafe.platform.HelpRequestComposer
import kotlin.test.assertEquals

class IosPlatformBoundaryTest {

    @Test
    fun helpRequestCompositionIsAvailableToIos() {
        assertEquals(
            "Help",
            HelpRequestComposer.composeMessage("Help", null),
        )
    }
}