package com.anisync.android.presentation.feed.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesomeMotion
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.anisync.android.R
import com.anisync.android.presentation.components.menu.Menu

/**
 * The feed's overflow: the two things that decide how the feed reads and have nowhere else to live.
 *
 * Grouping is a display choice this screen owns. The merge window is the account's own
 * `activityMergeTime` on AniList — it decides how the site merges the viewer's list updates before
 * they ever reach a feed — so the entry carries its current value and hands off to the screen that
 * already edits it rather than growing a second picker here.
 */
@Composable
internal fun FeedMenuButton(
    groupListUpdates: Boolean,
    mergeWindowLabel: String,
    onToggleGroupListUpdates: () -> Unit,
    onOpenActivitySettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.cd_feed_options),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
        }
        Menu(expanded = expanded, onDismissRequest = { expanded = false }) {
            item(
                text = stringResource(R.string.feed_menu_group_updates),
                supportingText = stringResource(R.string.feed_menu_group_updates_description),
                leadingIcon = Icons.Default.AutoAwesomeMotion,
                selected = groupListUpdates,
                onClick = {
                    expanded = false
                    onToggleGroupListUpdates()
                }
            )
            item(
                text = stringResource(R.string.feed_menu_merge_window),
                supportingText = stringResource(R.string.feed_menu_merge_window_description),
                leadingIcon = Icons.Default.Schedule,
                trailingLabel = mergeWindowLabel,
                onClick = {
                    expanded = false
                    onOpenActivitySettings()
                }
            )
        }
    }
}
