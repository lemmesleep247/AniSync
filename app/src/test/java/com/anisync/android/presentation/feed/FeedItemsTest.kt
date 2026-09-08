package com.anisync.android.presentation.feed

import com.anisync.android.domain.ActivityType
import com.anisync.android.domain.UserActivity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.ZoneId
import java.util.concurrent.TimeUnit

class FeedItemsTest {

    private val zone: ZoneId = ZoneId.of("UTC")

    /** 2026-09-05 12:00 UTC, the newest activity in these fixtures. */
    private val noon = 1_788_609_600_000L

    private fun listActivity(
        id: Int,
        userId: Int,
        minutesBeforeNoon: Long
    ) = UserActivity(
        id = id,
        type = ActivityType.MEDIA_LIST,
        userId = userId,
        userName = "user$userId",
        timestamp = noon - TimeUnit.MINUTES.toMillis(minutesBeforeNoon)
    )

    private fun status(id: Int, userId: Int, minutesBeforeNoon: Long) = UserActivity(
        id = id,
        type = ActivityType.TEXT,
        userId = userId,
        userName = "user$userId",
        text = "hello",
        timestamp = noon - TimeUnit.MINUTES.toMillis(minutesBeforeNoon)
    )

    @Test
    fun `one person's consecutive list updates collapse into a group`() {
        val items = buildFeedItems(
            activities = listOf(
                listActivity(id = 1, userId = 7, minutesBeforeNoon = 0),
                listActivity(id = 2, userId = 7, minutesBeforeNoon = 20),
                listActivity(id = 3, userId = 7, minutesBeforeNoon = 35)
            ),
            groupListUpdates = true,
            zone = zone
        )

        val group = items.filterIsInstance<FeedItem.Group>().single()
        assertEquals(listOf(1, 2, 3), group.activities.map { it.id })
        assertEquals(0, items.count { it is FeedItem.Single })
    }

    @Test
    fun `a gap wider than the window starts a new card`() {
        val items = buildFeedItems(
            activities = listOf(
                listActivity(id = 1, userId = 7, minutesBeforeNoon = 0),
                listActivity(id = 2, userId = 7, minutesBeforeNoon = 20),
                listActivity(id = 3, userId = 7, minutesBeforeNoon = 200)
            ),
            groupListUpdates = true,
            zone = zone
        )

        val group = items.filterIsInstance<FeedItem.Group>().single()
        assertEquals(listOf(1, 2), group.activities.map { it.id })
        assertEquals(listOf(3), items.filterIsInstance<FeedItem.Single>().map { it.activity.id })
    }

    @Test
    fun `another person or a status breaks the run`() {
        val items = buildFeedItems(
            activities = listOf(
                listActivity(id = 1, userId = 7, minutesBeforeNoon = 0),
                status(id = 2, userId = 7, minutesBeforeNoon = 5),
                listActivity(id = 3, userId = 7, minutesBeforeNoon = 10),
                listActivity(id = 4, userId = 8, minutesBeforeNoon = 15)
            ),
            groupListUpdates = true,
            zone = zone
        )

        assertTrue(items.none { it is FeedItem.Group })
        assertEquals(
            listOf(1, 2, 3, 4),
            items.filterIsInstance<FeedItem.Single>().map { it.activity.id }
        )
    }

    @Test
    fun `grouping off leaves every update on its own`() {
        val items = buildFeedItems(
            activities = listOf(
                listActivity(id = 1, userId = 7, minutesBeforeNoon = 0),
                listActivity(id = 2, userId = 7, minutesBeforeNoon = 20)
            ),
            groupListUpdates = false,
            zone = zone
        )

        assertEquals(2, items.count { it is FeedItem.Single })
    }

    @Test
    fun `each day gets its own header`() {
        val items = buildFeedItems(
            activities = listOf(
                listActivity(id = 1, userId = 7, minutesBeforeNoon = 0),
                listActivity(id = 2, userId = 7, minutesBeforeNoon = 30),
                status(id = 3, userId = 9, minutesBeforeNoon = 60 * 24),
                status(id = 4, userId = 9, minutesBeforeNoon = 60 * 25)
            ),
            groupListUpdates = true,
            zone = zone
        )

        val headers = items.filterIsInstance<FeedItem.DayHeader>()
        assertEquals(2, headers.size)
        assertEquals(2, headers.map { it.startOfDay }.distinct().size)
    }

    @Test
    fun `a run that crosses midnight does not group across the day marker`() {
        val items = buildFeedItems(
            activities = listOf(
                listActivity(id = 1, userId = 7, minutesBeforeNoon = 12 * 60 - 10),
                listActivity(id = 2, userId = 7, minutesBeforeNoon = 12 * 60 + 10)
            ),
            groupListUpdates = true,
            zone = zone
        )

        assertEquals(2, items.count { it is FeedItem.Single })
        assertEquals(2, items.count { it is FeedItem.DayHeader })
    }
}
