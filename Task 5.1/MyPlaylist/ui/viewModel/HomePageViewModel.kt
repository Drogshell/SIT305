package com.trevin.myplaylists.ui.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.trevin.myplaylists.data.PlaylistRepo
import com.trevin.myplaylists.data.model.PlaylistVideoData
import com.trevin.myplaylists.network.ApiConfig
import com.trevin.myplaylists.util.YouTubeUtility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomePageViewModel(private val playlistRepo: PlaylistRepo) : ViewModel(){

    private val _videos = MutableStateFlow<List<PlaylistVideoData>>(emptyList())
    val videos : StateFlow<List<PlaylistVideoData>> = _videos
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadVideosFor(userId: Long){
        viewModelScope.launch {
            _isLoading.value = true
            Log.d("HomeVM","▶️ loadVideosFor() called, isLoading=${_isLoading.value}")
            playlistRepo.getPlaylistFlow(userId).collect { item ->
                Log.d("HomeVM","   • rawList size = ${item.size}")
                val enrichedList = withContext(Dispatchers.IO) {
                    // One async per video so they run in parallel
                    coroutineScope {
                        val deferred = item.map { user ->
                            async {
                                YouTubeUtility.extractYouTubeId(user.youtubeUrl)?.let { videoID ->
                                    val response = ApiConfig.getService()
                                        .getVideoById(id = videoID)
                                        .execute()

                                    val item = response.body()?.items?.firstOrNull()
                                    item?.let {
                                        PlaylistVideoData(
                                            user.id,
                                            videoID,
                                            it.snippet.title,
                                            it.snippet.thumbnails.high.url,
                                            it.snippet.channelTitle,
                                            it.snippet.publishedAt,
                                            it.statistics.viewCount,
                                            it.snippet.channelId
                                        )
                                    }
                                }
                            }
                        }
                        deferred.awaitAll().filterNotNull()
                    }
                }
                Log.d("HomeVM","   • enrichedList size = ${enrichedList.size}")
                _videos.value = enrichedList
                _isLoading.value = false
                Log.d("HomeVM","✅ load complete, isLoading=${_isLoading.value}")
            }
        }
    }

    fun deletePlaylistItem(itemId: Long) {
        viewModelScope.launch {
            playlistRepo.remove(itemId)
        }
    }

    class Factory(private val repo: PlaylistRepo) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomePageViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return HomePageViewModel(repo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}