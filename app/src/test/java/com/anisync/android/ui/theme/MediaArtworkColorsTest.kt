package com.anisync.android.ui.theme

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MediaArtworkColorsTest {

    @Test
    fun `anilist hex parses as opaque seed`() {
        assertEquals(Color(0xFFE4A15D), mediaArtworkSeedColor("#e4a15d"))
        assertEquals(Color(0xFF1F6FD0), mediaArtworkSeedColor("1f6fd0"))
    }

    @Test
    fun `null and blank keep the app theme`() {
        assertNull(mediaArtworkSeedColor(null))
        assertNull(mediaArtworkSeedColor(""))
        assertNull(mediaArtworkSeedColor("   "))
    }

    @Test
    fun `malformed hex keeps the app theme`() {
        assertNull(mediaArtworkSeedColor("#abc"))
        assertNull(mediaArtworkSeedColor("#gggggg"))
        assertNull(mediaArtworkSeedColor("#80112233"))
    }

    @Test
    fun `washed out colors keep the app theme`() {
        assertNull(mediaArtworkSeedColor("#ffffff"))
        assertNull(mediaArtworkSeedColor("#000000"))
        assertNull(mediaArtworkSeedColor("#8e8e8e"))
        assertNull(mediaArtworkSeedColor("#a8a2a2"))
    }

    @Test
    fun `a faint but readable hue still seeds a palette`() {
        assertEquals(Color(0xFFA8A090), mediaArtworkSeedColor("#a8a090"))
    }
}
