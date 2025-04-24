package com.trevin.myplaylists.util

import java.text.SimpleDateFormat
import java.util.Locale

class YouTubeUtility {
    companion object{
        fun extractYouTubeId(url: String): String?{
            val regex = Regex("""(?:v=|youtu\.be/)([A-Za-z0-9_-]{11})""")
            return regex.find(url)?.groups?.get(1)?.value
        }

        fun formatDate(isoDate: String): String {
            return try {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                val date = parser.parse(isoDate)
                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                date?.let {
                    formatter.format(it)
                } ?: isoDate
            } catch (e: Exception){
                isoDate
            }
        }

        fun formatViewCount(rawCount: String): String{
            return try {
                val count = rawCount.toLong()
                when {
                    count >= 1_000_000 -> String.format(Locale.getDefault(), "%.1fM", count / 1_000_000.0)
                    count >= 1_000 -> String.format(Locale.getDefault(), "%.1fK", count / 1_000.0)
                    else -> "$count"
                }
            } catch (e: Exception) {
                "$rawCount views"
            }
        }

    }
}