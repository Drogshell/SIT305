package com.trevin.myplaylists.network.models

import com.google.gson.annotations.SerializedName

object YouTubeApiModels {

    data class VideoResponse(
        @SerializedName("nextPageToken") val nextPageToken: String?,
        @SerializedName("items") val items: List<Item>
    ) {
        data class Item(
            @SerializedName("id")       val id: String,
            @SerializedName("snippet")  val snippet: Snippet,
            @SerializedName("statistics") val statistics: Statistics
        )
    }

    data class ChannelResponse(
        @SerializedName("items") val items: List<Item>
    ) {
        data class Item(
            @SerializedName("id")                 val id: String,
            @SerializedName("snippet")            val snippet: Snippet,
            @SerializedName("statistics")         val statistics: Statistics
        )
    }

    data class Snippet(
        @SerializedName("title")        val title: String,
        @SerializedName("description")  val description: String,
        @SerializedName("channelId")    val channelId: String,
        @SerializedName("channelTitle") val channelTitle: String,
        @SerializedName("publishedAt")  val publishedAt: String,
        @SerializedName("thumbnails")   val thumbnails: Thumbnails
    )

    data class Thumbnails(
        @SerializedName("high") val high: High
    ) {
        data class High(@SerializedName("url") val url: String)
    }

    data class Statistics(
        @SerializedName("viewCount")       val viewCount: String,
        @SerializedName("subscriberCount") val subscriberCount: String?
    )
}