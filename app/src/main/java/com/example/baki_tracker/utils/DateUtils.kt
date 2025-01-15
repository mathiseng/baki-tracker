package com.example.baki_tracker.utils

import android.icu.util.Calendar
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

fun Timestamp.formatTimestampToString(): String {
    val date = this.toDate()

    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    return dateFormat.format(date)
}

fun getCurrentDateString(): String {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return dateFormat.format(Calendar.getInstance().time)
}