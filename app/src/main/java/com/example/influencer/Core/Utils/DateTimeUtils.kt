package com.example.influencer.Core.Utils


import com.google.firebase.Timestamp
import java.util.concurrent.TimeUnit

object DateTimeUtils {

    fun getTimeAgo(timestamp: Timestamp): String {
        val postDate = timestamp.toDate() // Convert Firebase Timestamp to java.util.Date
        val currentDate = java.util.Date()
        val diff = currentDate.time - postDate.time // Difference in milliseconds

        val days = TimeUnit.MILLISECONDS.toDays(diff)
        val months = days / 30
        val years = months / 12

        return when {
            years > 0 -> "$years years ago"     //TODO poner esos Strings de years ago desde Strings.xml
            months > 0 -> "$months months ago"
            else -> "$days days ago"
        }
    }
}