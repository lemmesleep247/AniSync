package com.anisync.android.presentation.util

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

/**
 * Hand a link to whatever the device uses for the web.
 *
 * Silently does nothing when there is no browser to take it — every caller is an optional detour to
 * anilist.co, never the only way to finish what the user started.
 */
fun openUrl(context: Context, url: String) {
    runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri())) }
}
