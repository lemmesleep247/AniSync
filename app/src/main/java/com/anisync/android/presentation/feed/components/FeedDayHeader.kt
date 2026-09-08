package com.anisync.android.presentation.feed.components

import android.text.format.DateUtils
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anisync.android.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

/**
 * Today, Yesterday, then the date — the same group header the Library screen uses over its rows.
 *
 * A feed with no marks reads as one undifferentiated scroll: the timestamps are relative, so
 * "22h ago" and "1d ago" sit next to each other with nothing saying a day turned over.
 */
@Composable
fun FeedDayHeader(
    startOfDay: Long,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val today = stringResource(R.string.feed_day_today)
    val yesterday = stringResource(R.string.feed_day_yesterday)
    val label = remember(startOfDay, today, yesterday) {
        when (daysAgo(startOfDay)) {
            0L -> today
            1L -> yesterday
            else -> DateUtils.formatDateTime(
                context,
                startOfDay,
                DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_ABBREV_MONTH
            )
        }
    }

    Text(
        text = label,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier.padding(top = 8.dp, bottom = 2.dp)
    )
}

private fun daysAgo(startOfDay: Long): Long {
    val zone = ZoneId.systemDefault()
    val day = Instant.ofEpochMilli(startOfDay).atZone(zone).toLocalDate()
    return ChronoUnit.DAYS.between(day, LocalDate.now(zone))
}
