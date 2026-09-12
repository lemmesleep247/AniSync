package com.anisync.android.presentation.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anisync.android.ui.theme.LocalExpressiveTypography

/**
 * MD3 Expressive HeroDashboard.
 *
 * Editorial layout (per m3.material.io/styles/typography/editorial-treatments):
 *  - eyebrow label (caps, wide tracking, low size)
 *  - mixed-weight baseline-aligned row: huge W900 numeric + W400 unit
 *  - optional editorial-lead sentence ("≈ 42 days of your life")
 *  - divider
 *  - up to three sub-stats using tabular figures
 *
 * Every number and label is sized against the width it actually gets, so a five-digit episode count
 * keeps all of its digits and a long translated label keeps all of its letters.
 */
@Composable
fun HeroDashboard(
    primaryValue: String,
    primaryUnit: String,
    primaryLabel: String,
    secondaryRow: List<EditorialStat>,
    accentText: String? = null,
    modifier: Modifier = Modifier
) {
    val expressive = LocalExpressiveTypography.current
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(28.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        BoxWithConstraints(
            modifier = Modifier.padding(
                start = 24.dp,
                end = 24.dp,
                top = 28.dp,
                bottom = 24.dp
            )
        ) {
            val contentWidth = maxWidth
            Column {
                Text(
                    text = primaryLabel.uppercase(),
                    style = expressive.statLabel,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
                Spacer(Modifier.height(4.dp))
                // The unit takes at most 40% of the row, and what is left is what the hero number
                // has to fit into.
                val unitStyle = MaterialTheme.typography.headlineSmall
                val unitWidth = measuredWidth(primaryUnit, unitStyle).coerceAtMost(contentWidth * 0.4f)
                val heroStyle = fittedTextStyle(
                    base = expressive.heroNumeric,
                    values = listOf(primaryValue),
                    maxWidth = contentWidth - unitWidth - 8.dp,
                    maxLines = 1,
                    minFontSize = 40.sp
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = primaryValue,
                        style = heroStyle,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = primaryUnit,
                        style = unitStyle,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                        softWrap = true,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .padding(bottom = 14.dp)
                    )
                }
                accentText?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = it,
                        style = expressive.editorialLead,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                    )
                }
                if (secondaryRow.isNotEmpty()) {
                    Spacer(Modifier.height(24.dp))
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.12f)
                    )
                    Spacer(Modifier.height(20.dp))
                    val spacing = 20.dp
                    val columnWidth = (contentWidth - spacing * (secondaryRow.size - 1)) / secondaryRow.size
                    // One size for the whole row, taken from the longest value, so the columns keep
                    // an even editorial rhythm instead of each number shrinking on its own. Labels
                    // are fitted the same way: German turns "mean score" into a 22-letter word that
                    // a fixed size cuts in half.
                    val valueStyle = fittedTextStyle(
                        base = expressive.statNumericMedium,
                        values = secondaryRow.map { it.value },
                        maxWidth = columnWidth,
                        maxLines = 1,
                        minFontSize = 20.sp
                    )
                    val labelStyle = fittedTextStyle(
                        base = expressive.statLabel,
                        values = secondaryRow.map { it.label.uppercase() },
                        maxWidth = columnWidth,
                        maxLines = LABEL_MAX_LINES,
                        minFontSize = 8.sp
                    )
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing)
                    ) {
                        secondaryRow.forEach { stat ->
                            EditorialStatBlock(stat, valueStyle, labelStyle, Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EditorialStatBlock(
    stat: EditorialStat,
    valueStyle: TextStyle,
    labelStyle: TextStyle,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        if (stat.icon != null) {
            Icon(
                imageVector = stat.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.height(4.dp))
        }
        Text(
            text = stat.value,
            style = valueStyle,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = stat.label.uppercase(),
            style = labelStyle,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
            maxLines = LABEL_MAX_LINES,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/** Width [text] needs on one line in [style]. */
@Composable
private fun measuredWidth(text: String, style: TextStyle): Dp {
    val measurer = rememberTextMeasurer()
    val density = LocalDensity.current
    return remember(text, style, density, measurer) {
        with(density) { measurer.measure(text, style).size.width.toDp() }
    }
}

/**
 * [base] stepped down until every one of [values] fits [maxWidth] within [maxLines], never below
 * [minFontSize]. Line height and tracking keep the ratios the base style declares.
 */
@Composable
private fun fittedTextStyle(
    base: TextStyle,
    values: List<String>,
    maxWidth: Dp,
    maxLines: Int,
    minFontSize: TextUnit
): TextStyle {
    val measurer = rememberTextMeasurer()
    val density = LocalDensity.current
    return remember(base, values, maxWidth, maxLines, minFontSize, density, measurer) {
        val constraints = Constraints(maxWidth = with(density) { maxWidth.roundToPx() }.coerceAtLeast(0))
        var size = base.fontSize.value
        var style = base
        while (true) {
            val overflows = values.any { value ->
                measurer.measure(
                    text = value,
                    style = style,
                    maxLines = maxLines,
                    constraints = constraints
                ).hasVisualOverflow
            }
            if (!overflows || size <= minFontSize.value) break
            size = (size - 1f).coerceAtLeast(minFontSize.value)
            style = base.scaledTo(size)
        }
        style
    }
}

/** [base] at [fontSize], with line height and tracking scaled by the same factor. */
private fun TextStyle.scaledTo(fontSize: Float): TextStyle {
    val scale = if (this.fontSize.isSp && this.fontSize.value > 0f) fontSize / this.fontSize.value else 1f
    return copy(
        fontSize = fontSize.sp,
        lineHeight = if (lineHeight.isSp) (lineHeight.value * scale).sp else lineHeight,
        letterSpacing = if (letterSpacing.isSp) (letterSpacing.value * scale).sp else letterSpacing
    )
}

/** A label longer than this in the narrowest column is shrunk rather than cut. */
private const val LABEL_MAX_LINES = 3

// region Previews

@Preview(showBackground = true, name = "HeroDashboard — typical anime", widthDp = 360)
@Composable
private fun HeroDashboardTypicalPreview() {
    StatPreviewSurface(isDark = false) {
        HeroDashboard(
            primaryValue = "1,248",
            primaryUnit = "episodes",
            primaryLabel = "Watched",
            accentText = "≈ 42 days of your life",
            secondaryRow = listOf(
                EditorialStat("128", "Total", Icons.Default.PlayArrow),
                EditorialStat("8.1", "Mean", Icons.Default.Star),
                EditorialStat("1.4", "σ")
            )
        )
    }
}

@Preview(showBackground = true, name = "HeroDashboard — five-digit sub-stats", widthDp = 360)
@Composable
private fun HeroDashboardLongValuesPreview() {
    StatPreviewSurface(isDark = false) {
        HeroDashboard(
            primaryValue = "1005",
            primaryUnit = "anime",
            primaryLabel = "Total anime",
            accentText = "≈ 420.0 days of your life",
            secondaryRow = listOf(
                EditorialStat("25204", "Episodes", Icons.Default.PlayArrow),
                EditorialStat("72.87", "Mean score", Icons.Default.Star),
                EditorialStat("15.14", "Standard deviation")
            )
        )
    }
}

@Preview(showBackground = true, name = "HeroDashboard — long translated labels", widthDp = 360)
@Composable
private fun HeroDashboardLongLabelsPreview() {
    StatPreviewSurface(isDark = false) {
        HeroDashboard(
            primaryValue = "242",
            primaryUnit = "anime",
            primaryLabel = "Anime gesamt",
            accentText = "≈ 120,8 Tage deines Lebens",
            secondaryRow = listOf(
                EditorialStat("7760", "Episoden", Icons.Default.PlayArrow),
                EditorialStat("85,9", "Durchschnittsbewertung", Icons.Default.Star),
                EditorialStat("9,8", "Standardabweichung")
            )
        )
    }
}

@Preview(showBackground = true, name = "HeroDashboard — no accent + long unit", widthDp = 360)
@Composable
private fun HeroDashboardNoAccentLongUnitPreview() {
    StatPreviewSurface(isDark = false) {
        HeroDashboard(
            primaryValue = "42",
            primaryUnit = "ridiculously-long-unit-name",
            primaryLabel = "Read",
            accentText = null,
            secondaryRow = listOf(
                EditorialStat("12", "Volumes"),
                EditorialStat("7.9", "Mean")
            )
        )
    }
}

@Preview(showBackground = true, name = "HeroDashboard — empty secondary row", widthDp = 360)
@Composable
private fun HeroDashboardEmptySecondaryPreview() {
    StatPreviewSurface(isDark = false) {
        HeroDashboard(
            primaryValue = "0",
            primaryUnit = "entries",
            primaryLabel = "Library",
            secondaryRow = emptyList()
        )
    }
}

@Preview(showBackground = true, name = "HeroDashboard — dark", widthDp = 360, heightDp = 320)
@Composable
private fun HeroDashboardDarkPreview() {
    StatPreviewSurface(isDark = true) {
        HeroDashboard(
            primaryValue = "1,248",
            primaryUnit = "episodes",
            primaryLabel = "Watched",
            accentText = "≈ 42 days of your life",
            secondaryRow = listOf(
                EditorialStat("128", "Total", Icons.Default.PlayArrow),
                EditorialStat("8.1", "Mean", Icons.Default.Star),
                EditorialStat("1.4", "σ")
            )
        )
    }
}

// endregion
