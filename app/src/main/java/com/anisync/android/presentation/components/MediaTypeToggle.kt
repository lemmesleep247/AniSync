package com.anisync.android.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Tv
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.anisync.android.R
import com.anisync.android.presentation.util.LocalAdaptiveInfo
import com.anisync.android.presentation.util.rememberHapticFeedback
import com.anisync.android.type.MediaType

/** Height of the rails this toggle sits in, on Library, Discover and Feed. */
val MediaTypeToggleHeight = ConnectedToggleDefaults.Height

/**
 * Anime and manga as two segments rather than a full-width group.
 *
 * Wider windows have the room to spell the two words out, so they do; a phone rail does not, and
 * keeps the icons alone beside the chips that share its row. Both cases sit in the same 40dp
 * target. The shapes come from [ConnectedToggleSegment], which the feed's scope switch also uses.
 *
 * Shared by the Library rail, Discover's browse rail and the Feed rail so they cannot drift apart.
 */
@Composable
fun MediaTypeToggle(
    selected: MediaType,
    onSelect: (MediaType) -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = MediaTypeToggleHeight
) {
    val haptic = rememberHapticFeedback()
    val showLabels = !LocalAdaptiveInfo.current.isCompact
    val segmentModifier = if (showLabels) {
        Modifier
    } else {
        Modifier.width(ConnectedToggleDefaults.IconOnlyWidth)
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(ConnectedToggleDefaults.Spacing)
    ) {
        ConnectedToggleSegment(
            icon = Icons.Default.Tv,
            label = stringResource(R.string.media_type_anime),
            selected = selected == MediaType.ANIME,
            leading = true,
            showLabel = showLabels,
            height = height,
            modifier = segmentModifier,
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onSelect(MediaType.ANIME)
            }
        )
        ConnectedToggleSegment(
            icon = Icons.AutoMirrored.Filled.MenuBook,
            label = stringResource(R.string.media_type_manga),
            selected = selected == MediaType.MANGA,
            leading = false,
            showLabel = showLabels,
            height = height,
            modifier = segmentModifier,
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onSelect(MediaType.MANGA)
            }
        )
    }
}
