package com.trevin.myplaylists.data.model

data class PlaylistVideoData(
    val playListItemId: Long,
    val videoId: String,
    val title: String,
    val thumbnailUrl: String,
    val author: String,
    val publishedAt: String,
    val viewCount: String,
    val channelId: String
)
