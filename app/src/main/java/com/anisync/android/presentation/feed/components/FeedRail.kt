package com.anisync.android.presentation.feed.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material.icons.filled.DynamicFeed
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anisync.android.R
import com.anisync.android.domain.FeedFilter
import com.anisync.android.domain.FeedMediaType
import com.anisync.android.domain.FeedScope
import com.anisync.android.presentation.components.ConnectedToggleDefaults
import com.anisync.android.presentation.components.ConnectedToggleSegment
import com.anisync.android.presentation.util.bouncyClickable
import com.anisync.android.presentation.util.rememberHapticFeedback

private val RailHeight = ConnectedToggleDefaults.Height

/**
 * The whole feed header: which stream, then what kind of activity in it.
 *
 * The shipped screen spent a full-width scope group and a chip row on this, 114dp of chrome that
 * never scrolled away and shifted under you whenever the media toggle appeared and disappeared.
 * Scope keeps a row of its own, since it is the question asked most and it hosts the overflow.
 *
 * The rail below it is one axis, not two. Media type belongs to list activity alone, so pairing a
 * type toggle with the activity chips left it answering only one of them and dead under the other
 * two. The four chips say what each one actually selects.
 */
@Composable
fun FeedRail(
    scope: FeedScope,
    filter: FeedFilter,
    mediaType: FeedMediaType,
    groupListUpdates: Boolean,
    mergeWindowLabel: String,
    onScopeChange: (FeedScope) -> Unit,
    onFilterChange: (FeedFilter) -> Unit,
    onListTypeChange: (FeedMediaType) -> Unit,
    onToggleGroupListUpdates: () -> Unit,
    onOpenActivitySettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FeedScopeRow(
            scope = scope,
            groupListUpdates = groupListUpdates,
            mergeWindowLabel = mergeWindowLabel,
            onScopeChange = onScopeChange,
            onToggleGroupListUpdates = onToggleGroupListUpdates,
            onOpenActivitySettings = onOpenActivitySettings
        )
        FeedFilterRail(
            filter = filter,
            mediaType = mediaType,
            onFilterChange = onFilterChange,
            onListTypeChange = onListTypeChange
        )
    }
}

/**
 * Global or Following, on the connected shapes the media toggle uses, plus the feed's overflow.
 *
 * The two segments split the row rather than hugging their words: the space is theirs, and a wide
 * target is the point of a switch that carries the whole screen.
 */
@Composable
private fun FeedScopeRow(
    scope: FeedScope,
    groupListUpdates: Boolean,
    mergeWindowLabel: String,
    onScopeChange: (FeedScope) -> Unit,
    onToggleGroupListUpdates: () -> Unit,
    onOpenActivitySettings: () -> Unit
) {
    val haptic = rememberHapticFeedback()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 8.dp)
            .height(RailHeight),
        horizontalArrangement = Arrangement.spacedBy(ConnectedToggleDefaults.Spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ConnectedToggleSegment(
            icon = Icons.Default.Group,
            label = stringResource(R.string.feed_scope_following),
            selected = scope == FeedScope.FOLLOWING,
            leading = true,
            modifier = Modifier.weight(1f),
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onScopeChange(FeedScope.FOLLOWING)
            }
        )
        ConnectedToggleSegment(
            icon = Icons.Default.Public,
            label = stringResource(R.string.feed_scope_global),
            selected = scope == FeedScope.GLOBAL,
            leading = false,
            modifier = Modifier.weight(1f),
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onScopeChange(FeedScope.GLOBAL)
            }
        )
        Spacer(Modifier.width(4.dp))
        FeedMenuButton(
            groupListUpdates = groupListUpdates,
            mergeWindowLabel = mergeWindowLabel,
            onToggleGroupListUpdates = onToggleGroupListUpdates,
            onOpenActivitySettings = onOpenActivitySettings
        )
    }
}

/**
 * One pinned row carrying both "which media" and "which kind of activity".
 *
 * Only a list update carries a media type, so the toggle answers the List chip alone. Under All and
 * Status it dims rather than vanishing: a rail that changes shape with the filter is the layout
 * shift this row was built to remove.
 */
@Composable
private fun FeedFilterRail(
    filter: FeedFilter,
    mediaType: FeedMediaType,
    onFilterChange: (FeedFilter) -> Unit,
    onListTypeChange: (FeedMediaType) -> Unit
) {
    val background = MaterialTheme.colorScheme.background
    val chips = remember { FeedChip.entries }

    Box(modifier = Modifier.fillMaxWidth().height(RailHeight)) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(chips, key = { it.name }) { chip ->
                FeedFilterChip(
                    chip = chip,
                    selected = chip.isSelected(filter, mediaType),
                    onClick = {
                        when (chip) {
                            FeedChip.ALL -> onFilterChange(FeedFilter.ALL)
                            FeedChip.STATUS -> onFilterChange(FeedFilter.STATUS)
                            FeedChip.ANIME -> onListTypeChange(FeedMediaType.ANIME)
                            FeedChip.MANGA -> onListTypeChange(FeedMediaType.MANGA)
                        }
                    }
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(24.dp)
                .height(RailHeight)
                .background(Brush.horizontalGradient(listOf(Color.Transparent, background)))
        )
    }
}

/** The four things the rail can select, in the order they narrow the feed. */
private enum class FeedChip { ALL, STATUS, ANIME, MANGA }

private fun FeedChip.isSelected(filter: FeedFilter, mediaType: FeedMediaType): Boolean = when (this) {
    FeedChip.ALL -> filter == FeedFilter.ALL
    FeedChip.STATUS -> filter == FeedFilter.STATUS
    FeedChip.ANIME -> filter == FeedFilter.LIST && mediaType == FeedMediaType.ANIME
    FeedChip.MANGA -> filter == FeedFilter.LIST && mediaType == FeedMediaType.MANGA
}

@Composable
private fun FeedFilterChip(
    chip: FeedChip,
    selected: Boolean,
    onClick: () -> Unit
) {
    val haptic = rememberHapticFeedback()
    val container by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.secondaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainerHigh
        },
        label = "FeedChipContainer"
    )
    val content by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.onSecondaryContainer
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        label = "FeedChipContent"
    )
    val label = stringResource(chip.labelRes())

    Surface(
        color = container,
        shape = CircleShape,
        modifier = Modifier
            .height(RailHeight)
            .bouncyClickable(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onClick()
                },
                role = Role.Tab,
                clipShape = CircleShape
            )
            .clearAndSetSemantics {
                role = Role.Tab
                this.selected = selected
                contentDescription = label
            }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = chip.icon(),
                contentDescription = null,
                tint = content,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                color = content,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}

private fun FeedChip.icon(): ImageVector = when (this) {
    FeedChip.ALL -> Icons.Default.DynamicFeed
    FeedChip.STATUS -> Icons.AutoMirrored.Outlined.Notes
    FeedChip.ANIME -> Icons.Default.Tv
    FeedChip.MANGA -> Icons.AutoMirrored.Filled.MenuBook
}

private fun FeedChip.labelRes(): Int = when (this) {
    FeedChip.ALL -> R.string.feed_filter_all
    FeedChip.STATUS -> R.string.feed_filter_status
    FeedChip.ANIME -> R.string.media_type_anime
    FeedChip.MANGA -> R.string.media_type_manga
}
