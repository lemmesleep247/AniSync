package com.anisync.android.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anisync.android.R
import com.anisync.android.domain.LibraryPriority
import com.anisync.android.presentation.util.toIconRes
import com.anisync.android.presentation.util.toLabel

/** One cell of a [CoverBadgeRibbon]. */
@Immutable
data class CoverBadge(
    @DrawableRes val icon: Int,
    val container: Color,
    val content: Color,
    val contentDescription: String?
)

/**
 * The stack of marks welded into a cover's top-left corner.
 *
 * Only the last cell rounds its bottom-end corner; anything above it stays square, so two marks
 * fuse into one shape instead of reading as two stickers stuck on the artwork. Stating the rule by
 * position rather than by badge means a third mark later inherits it without touching this.
 *
 * Order is fixed and not by importance: notes has been on this corner since #75 and moving it would
 * shift a mark people already read.
 */
@Composable
fun CoverBadgeRibbon(
    badges: List<CoverBadge>,
    modifier: Modifier = Modifier,
    iconSize: Dp = 14.dp,
    cellPadding: Dp = 4.dp
) {
    if (badges.isEmpty()) return
    Column(modifier = modifier) {
        badges.forEachIndexed { index, badge ->
            Surface(
                shape = if (index == badges.lastIndex) {
                    RoundedCornerShape(bottomEnd = 8.dp)
                } else {
                    RoundedCornerShape(0.dp)
                },
                color = badge.container
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(badge.icon),
                    contentDescription = badge.contentDescription,
                    tint = badge.content,
                    modifier = Modifier.padding(cellPadding).size(iconSize)
                )
            }
        }
    }
}

/** The notes mark: this entry carries a note worth spotting without opening it (#75). */
@Composable
fun notesBadge(): CoverBadge = CoverBadge(
    icon = R.drawable.ic_note_stack_24px,
    container = MaterialTheme.colorScheme.primaryContainer,
    content = MaterialTheme.colorScheme.onPrimaryContainer,
    contentDescription = stringResource(R.string.a11y_has_notes)
)

/**
 * The priority mark, or null for [LibraryPriority.LOW].
 *
 * Low is also what an entry that has never carried a priority reads as, so marking it would tag a
 * whole library and say nothing.
 */
@Composable
fun priorityBadge(priority: LibraryPriority): CoverBadge? {
    if (priority == LibraryPriority.LOW) return null
    val high = priority == LibraryPriority.HIGH
    return CoverBadge(
        icon = priority.toIconRes(),
        container = if (high) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.secondaryContainer
        },
        content = if (high) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSecondaryContainer
        },
        contentDescription = stringResource(R.string.a11y_priority, priority.toLabel())
    )
}

/** The ribbon a library cover carries, in fixed order. Empty when there is nothing to say. */
@Composable
fun coverBadges(hasNotes: Boolean, priority: LibraryPriority? = null): List<CoverBadge> =
    buildList {
        if (hasNotes) add(notesBadge())
        priority?.let { level -> priorityBadge(level)?.let(::add) }
    }
