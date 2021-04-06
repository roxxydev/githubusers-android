package com.githubusers.android.domain.util

import android.annotation.SuppressLint
import com.githubusers.android.domain.model.User
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

object Util {

    @SuppressLint("NewApi")
    fun dateTimestampStringToDateText(dateStr: String, displayPattern: String?): String {
        val myDate: Instant = Instant.parse(dateStr)
        val timestamp = myDate.toEpochMilli()
        return timestampToDateText(timestamp, displayPattern)
    }

    @SuppressLint("NewApi")
    fun timestampToDateText(timestamp: Long?, displayPattern: String?): String {
        var timestampMillis = timestamp ?: Instant.now().toEpochMilli()
        val instant = Instant.ofEpochMilli(timestampMillis)
        val pattern = displayPattern ?: "MMM d, h:mm a"
        val formatter = DateTimeFormatter.ofPattern(pattern)
            .withLocale(Locale.getDefault())
            .withZone(ZoneId.systemDefault())

        return formatter.format(instant)
    }

    fun datePickerTimestampToDateText(timestamp: Long): String {
        val format = SimpleDateFormat("EEE, MMM dd, yyyy")
        val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        utc.timeInMillis = timestamp
        return format.format(utc.time)
    }

    fun timePickerToTimeText(hour: Int, minute: Int): String {
        val format = SimpleDateFormat("h:mm a")
        val utc = Calendar.getInstance()
        utc.set(Calendar.HOUR_OF_DAY, hour)
        utc.set(Calendar.MINUTE, minute)
        return format.format(utc.time)
    }

    fun findUserPositionInList(users: List<User>, userToFind: User): Int {
        return users.indexOfFirst {
            it.login == userToFind.login
        }
    }
}