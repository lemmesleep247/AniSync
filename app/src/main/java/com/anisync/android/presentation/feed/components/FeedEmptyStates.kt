package com.anisync.android.presentation.feed.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DynamicFeed
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.anisync.android.R
import com.anisync.android.domain.FeedFilter
import com.anisync.android.domain.FeedMediaType
import com.anisync.android.domain.FeedScope
import com.anisync.android.presentation.components.EmptyState

/**
 * Nothing came back at all: the network, or AniList throttling us.
 *
 * Same mark and same way out as Discover's offline state, since two screens failing for one reason
 * should not look like two different problems. Only the sentence changes, because "check your
 * connection" is bad advice when the connection is fine and the app is simply asking too fast.
 */
@Composable
fun FeedOfflineState(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    rateLimited: Boolean = false
) {
    EmptyState(
        icon = Icons.Default.Public,
        title = stringResource(R.string.feed_empty_offline_title),
        description = stringResource(
            if (rateLimited) {
                R.string.profile_rate_limited_error
            } else {
                R.string.feed_empty_offline_desc
            }
        ),
        actionLabel = stringResource(R.string.retry),
        actionIcon = Icons.Default.Refresh,
        onAction = onRetry,
        emblemShape = RoundedCornerShape(22.dp),
        emblemContainer = MaterialTheme.colorScheme.secondaryContainer,
        emblemContent = MaterialTheme.colorScheme.onSecondaryContainer,
        modifier = modifier
    )
}

/**
 * The feed loaded and had nothing to show, which happens for three different reasons.
 *
 * Only the filtered case is the viewer's own doing and undone in one tap, so only it takes the
 * emphasised action — the same rule the library and Discover follow.
 */
@Composable
fun FeedEmptyState(
    scope: FeedScope,
    filter: FeedFilter,
    mediaType: FeedMediaType,
    onSwitchToGlobal: () -> Unit,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    // The rail selects one thing at a time, so the state can name it.
    val chipLabel = stringResource(
        when (filter) {
            FeedFilter.ALL -> R.string.feed_filter_all
            FeedFilter.STATUS -> R.string.feed_filter_status
            FeedFilter.LIST -> when (mediaType) {
                FeedMediaType.ANIME -> R.string.media_type_anime
                FeedMediaType.MANGA -> R.string.media_type_manga
            }
        }
    )

    when {
        filter != FeedFilter.ALL -> EmptyState(
            icon = Icons.Default.Tune,
            title = stringResource(R.string.feed_empty_filtered_title, chipLabel),
            description = stringResource(R.string.feed_empty_filtered_body, chipLabel),
            actionLabel = stringResource(R.string.feed_empty_clear_filters),
            actionIcon = Icons.Default.Close,
            onAction = onClearFilters,
            emblemShape = RoundedCornerShape(22.dp),
            emblemContainer = MaterialTheme.colorScheme.secondaryContainer,
            emblemContent = MaterialTheme.colorScheme.onSecondaryContainer,
            actionEmphasised = true,
            animationKey = filter,
            modifier = modifier
        )

        scope == FeedScope.FOLLOWING -> EmptyState(
            icon = Icons.Default.Group,
            title = stringResource(R.string.feed_empty_following_title),
            description = stringResource(R.string.feed_empty_following),
            actionLabel = stringResource(R.string.feed_empty_following_action),
            actionIcon = Icons.Default.Public,
            onAction = onSwitchToGlobal,
            emblemShape = RoundedCornerShape(22.dp),
            emblemContainer = MaterialTheme.colorScheme.secondaryContainer,
            emblemContent = MaterialTheme.colorScheme.onSecondaryContainer,
            animationKey = scope,
            modifier = modifier
        )

        else -> EmptyState(
            icon = Icons.Default.DynamicFeed,
            title = stringResource(R.string.feed_empty_global_title),
            description = stringResource(R.string.feed_empty_global),
            emblemShape = RoundedCornerShape(22.dp),
            emblemContainer = MaterialTheme.colorScheme.secondaryContainer,
            emblemContent = MaterialTheme.colorScheme.onSecondaryContainer,
            animationKey = scope,
            modifier = modifier
        )
    }
}
