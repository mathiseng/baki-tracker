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

fun convertToLong(dateString: String, timeString: String): Long {
    try {
        // Create a SimpleDateFormat for the date and time
        val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

        // Parse the date and time strings into Date objects
        val date = dateFormatter.parse(dateString)
        val time = timeFormatter.parse(timeString)

        if (date != null && time != null) {
            // Use Calendar to combine the date and time into a single timestamp
            val calendar = Calendar.getInstance()
            calendar.time = date // Set the base date
            val timeCalendar = Calendar.getInstance()
            timeCalendar.time = time // Set the time

            // Apply hours and minutes from time to the base date
            calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY))
            calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            // Return the time in milliseconds
            return calendar.timeInMillis
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    // Return -1 if parsing failed
    return System.currentTimeMillis()
}