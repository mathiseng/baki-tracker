package com.example.baki_tracker.utils

import android.icu.util.Calendar
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun Timestamp.formatTimestampToString(): String {
    val date = this.toDate()

    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    return dateFormat.format(date)
}

fun getCurrentDateString(): String {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return dateFormat.format(Calendar.getInstance().time)
}

fun formatLongDateToFormattedDateString(
    currentMillis: Long, outputPattern: String = "dd.MM.yyyy"
): String {
    val sdf = SimpleDateFormat(outputPattern, Locale.getDefault())
    val resultdate = Date(currentMillis)
    return sdf.format(resultdate)
}

fun formatDateToUTC(date: Date): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    return dateFormat.format(date)
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatUtcToLocalTime(utcTime: String, outputPattern: String = "HH:mm"): String {
    return try {
        // Parse the UTC time string to a ZonedDateTime
        val utcDateTime = ZonedDateTime.parse(utcTime, DateTimeFormatter.ISO_DATE_TIME)

        // Convert to local timezone
        val localDateTime = utcDateTime.withZoneSameInstant(ZoneId.systemDefault())

        // Format the local datetime to the desired pattern
        localDateTime.format(DateTimeFormatter.ofPattern(outputPattern))
    } catch (e: Exception) {
        "Invalid Date"
    }
}