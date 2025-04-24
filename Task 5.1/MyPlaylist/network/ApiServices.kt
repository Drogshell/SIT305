package com.trevin.myplaylists.network

import com.trevin.myplaylists.network.models.YouTubeApiModels.ChannelResponse
import com.trevin.myplaylists.network.models.YouTubeApiModels.VideoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServices {

    @GET("videos")
    fun getVideoById(
        @Query("part") part: String = "snippet,statistics",
        @Query("id") id: String
    ): Call<VideoResponse>

    @GET("channels")
    fun getChannel(
        @Query("part") part: String = "snippet,brandingSettings,statistics",
        @Query("id") id: String
    ) : Call<ChannelResponse>

}