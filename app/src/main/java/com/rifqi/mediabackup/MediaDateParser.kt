package com.rifqi.mediabackup

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.regex.Pattern

object MediaDateParser {
    private val PATTERN = Pattern.compile(
        "^(?:IMG|VID)_(\\d{4}\\d{2}\\d{2}_\\d{2}\\d{2}\\d{2})",
        Pattern.CASE_INSENSITIVE
    )
    private val DATE_FORMAT = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)

    fun parseTimestamp(fileName: String, fallbackLastModified: Long): Long {
        val matcher = PATTERN.matcher(fileName)
        if (matcher.find()) {
            val dateStr = matcher.group(1)
            try {
                val date = dateStr?.let { DATE_FORMAT.parse(it) }
                if (date != null) return date.time
            } catch (_: Exception) {}
        }
        return fallbackLastModified
    }
}