package com.anisync.android.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anisync.android.presentation.util.bouncyClickable

/** Shared metrics for the app's two-segment switches. */
object ConnectedToggleDefaults {
    /** Height of the rails these toggles sit in. */
    val Height = 40.dp

    /** Width of a segment that carries its icon alone, on compact widths. */
    val IconOnlyWidth = 44.dp

    /** The seam between the two segments. */
    val Spacing = 2.dp

    private val OuterCorner = 17.dp
    private val SeamCorner = 7.dp

    /** Full radius on the selected end, a tight corner on the seam — the connected-group shapes. */
    fun shape(leading: Boolean, selected: Boolean): Shape = when {
        selected -> CircleShape
        leading -> RoundedCornerShape(
            topStart = OuterCorner,
            bottomStart = OuterCorner,
            topEnd = SeamCorner,
            bottomEnd = SeamCorner
        )
        else -> RoundedCornerShape(
            topStart = SeamCorner,
            bottomStart = SeamCorner,
            topEnd = OuterCorner,
            bottomEnd = OuterCorner
        )
    }
}

/**
 * One half of a two-way switch, drawn the way the rest of the app draws its switchers.
 *
 * The caller owns the width: a rail that shares its row gives the segment
 * [ConnectedToggleDefaults.IconOnlyWidth] and drops the label, a row of its own gives it a weight
 * and keeps the words. [MediaTypeToggle] and the feed's scope switch are both built from this, so
 * the two cannot drift apart.
 */
@Composable
fun ConnectedToggleSegment(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    leading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true,
    height: Dp = ConnectedToggleDefaults.Height
) {
    val shape = ConnectedToggleDefaults.shape(leading = leading, selected = selected)
    val content = if (selected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    Surface(
        color = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceContainer
        },
        shape = shape,
        modifier = modifier
            .height(height)
            .bouncyClickable(
                onClick = onClick,
                role = Role.Tab,
                clipShape = shape
            )
            .clearAndSetSemantics {
                role = Role.Tab
                this.selected = selected
                contentDescription = label
            }
    ) {
        Row(
            modifier = if (showLabel) Modifier.padding(horizontal = 14.dp) else Modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = content,
                modifier = Modifier.size(18.dp)
            )
            if (showLabel) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                    color = content,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }
    }
}