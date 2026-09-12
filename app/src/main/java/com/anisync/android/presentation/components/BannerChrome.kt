package com.anisync.android.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Top-bar chrome for a detail screen whose header is a banner. While the bar is transparent the
 * icons sit on artwork of any brightness, so each one carries a scrim disc and turns white; once the
 * bar is opaque they take the theme's colours. Shared so media, character, staff and studio details
 * cannot drift apart.
 */
@Composable
fun bannerChromeTint(overBanner: Boolean): Color = animateColorAsState(
    if (overBanner) Color.White else MaterialTheme.colorScheme.onSurface,
    label = "bannerChromeTint"
).value

/** Scrim disc behind [bannerChromeTint]; transparent once the chrome is off the banner. */
@Composable
fun bannerChromeContainer(overBanner: Boolean): Color = animateColorAsState(
    if (overBanner) Color.Black.copy(alpha = 0.36f) else Color.Transparent,
    label = "bannerChromeScrim"
).value

/** [bannerChromeTint] and [bannerChromeContainer] as `IconButton` colours. */
@Composable
fun bannerChromeColors(overBanner: Boolean): IconButtonColors =
    IconButtonDefaults.iconButtonColors(
        containerColor = bannerChromeContainer(overBanner),
        contentColor = bannerChromeTint(overBanner)
    )

/** How far a banner's status-bar scrim reaches: the bar itself plus a short dissolve. */
@Composable
fun bannerScrimHeight(): Dp =
    WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + BannerScrimFade

/**
 * Darkens the whole status-bar band before dissolving, so the system icons stay readable over a
 * bright banner in either theme. A plain top-to-transparent ramp over the same span is already a
 * third as strong by the time it reaches the clock.
 */
@Composable
fun bannerScrimBrush(height: Dp = bannerScrimHeight()): Brush = remember(height) {
    val hold = (1f - BannerScrimFade / height).coerceIn(0f, 1f)
    Brush.verticalGradient(
        0f to Color.Black.copy(alpha = 0.55f),
        hold to Color.Black.copy(alpha = 0.40f),
        1f to Color.Transparent
    )
}

private val BannerScrimFade = 28.dp
