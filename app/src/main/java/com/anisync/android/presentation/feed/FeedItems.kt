package com.anisync.android.presentation.feed

import androidx.compose.runtime.Immutable
import com.anisync.android.domain.ActivityType
import com.anisync.android.domain.UserActivity
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import java.time.Instant
import java.time.ZoneId
import java.util.concurrent.TimeUnit

/** A row of the feed: a day marker, one activity, or a run of list updates from one person. */
@Immutable
sealed interface FeedItem {
    val key: String

    /** Start of a day the feed below it belongs to. */
    data class DayHeader(val startOfDay: Long) : FeedItem {
        override val key: String get() = "day_$startOfDay"
    }

    data class Single(val activity: UserActivity) : FeedItem {
        override val key: String get() = "activity_${activity.id}"
    }

    /**
     * Consecutive list updates from one person, close enough together to read as one sitting.
     * [activities] keeps the feed's order, so the newest of the run is [author].
     */
    data class Group(val activities: ImmutableList<UserActivity>) : FeedItem {
        val author: UserActivity get() = activities.first()
        override val key: String get() = "group_${author.id}"
    }
}

/** How close together one person's list updates have to be to collapse into a single card. */
private val GroupWindowMillis = TimeUnit.HOURS.toMillis(1)

/** Two updates already repeat the author's name and avatar, which is what the card removes. */
private const val MinGroupSize = 2

/**
 * Turns a page of activity into what the feed actually draws.
 *
 * Two things happen here rather than in the list: the run of list updates one person posts while
 * catching up on a series collapses into a single card, and each day gets a marker so a long scroll
 * keeps its place in time. Both are display choices — [activities] stays the source of truth for
 * likes, edits and pagination.
 */
fun buildFeedItems(
    activities: List<UserActivity>,
    groupListUpdates: Boolean,
    zone: ZoneId = ZoneId.systemDefault()
): ImmutableList<FeedItem> {
    if (activities.isEmpty()) return persistentListOf()

    val items = mutableListOf<FeedItem>()
    var index = 0
    var currentDay: Long? = null

    while (index < activities.size) {
        val activity = activities[index]
        val day = activity.timestamp.startOfDay(zone)
        if (day != currentDay) {
            currentDay = day
            items.add(FeedItem.DayHeader(day))
        }

        val run = if (groupListUpdates) activities.groupRunAt(index, day, zone) else emptyList()
        if (run.size >= MinGroupSize) {
            items.add(FeedItem.Group(run.toImmutableList()))
            index += run.size
        } else {
            items.add(FeedItem.Single(activity))
            index++
        }
    }

    return items.toImmutableList()
}

/**
 * The run of list updates starting at [start] that belong to the same person, the same day and the
 * same [GroupWindowMillis] window. Empty when the activity at [start] cannot open a group.
 */
private fun List<UserActivity>.groupRunAt(
    start: Int,
    day: Long,
    zone: ZoneId
): List<UserActivity> {
    val first = this[start]
    if (first.type != ActivityType.MEDIA_LIST || first.userId == null) return emptyList()

    var end = start + 1
    while (end < size) {
        val next = this[end]
        // The gap that matters is to the update before it, not to the top of the run: catching up
        // on a whole season is one sitting even when it takes an evening.
        val sameRun = next.type == ActivityType.MEDIA_LIST &&
            next.userId == first.userId &&
            next.timestamp.startOfDay(zone) == day &&
            this[end - 1].timestamp - next.timestamp <= GroupWindowMillis
        if (!sameRun) break
        end++
    }
    return subList(start, end)
}

private fun Long.startOfDay(zone: ZoneId): Long =
    Instant.ofEpochMilli(this).atZone(zone).toLocalDate().atStartOfDay(zone).toInstant().toEpochMilli()
