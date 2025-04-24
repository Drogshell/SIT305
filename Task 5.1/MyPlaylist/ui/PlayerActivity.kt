package com.trevin.myplaylists.ui

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.trevin.myplaylists.databinding.ActivityVideoPlayerBinding
import com.trevin.myplaylists.network.ApiConfig
import com.trevin.myplaylists.network.models.YouTubeApiModels.ChannelResponse
import com.trevin.myplaylists.util.YouTubeUtility
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlayerActivity : AppCompatActivity() {

    private lateinit var playerView: YouTubePlayerView
    private lateinit var binding: ActivityVideoPlayerBinding
    private lateinit var fullscreenContainer: FrameLayout
    private lateinit var youTubePlayer: YouTubePlayer
    private var isFullscreen = false

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (isFullscreen) {
                youTubePlayer.toggleFullscreen()
            } else {
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        onBackPressedDispatcher.addCallback(onBackPressedCallback)

        playerView = binding.youtubePlayerView
        fullscreenContainer = binding.fullScreenViewContainer

        val iFrameOptions = IFramePlayerOptions.Builder().controls(1).fullscreen(1).build()

        playerView.enableAutomaticInitialization = false

        playerView.addFullscreenListener(object : FullscreenListener{
            override fun onEnterFullscreen(
                fullscreenView: View,
                exitFullscreen: () -> Unit
            ) {
                isFullscreen = true
                playerView.visibility = View.GONE
                fullscreenContainer.visibility = View.VISIBLE
                fullscreenContainer.addView(fullscreenView)
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }

            override fun onExitFullscreen() {
                isFullscreen = false
                fullscreenContainer.removeAllViews()
                fullscreenContainer.visibility = View.GONE
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }

        })

        playerView.initialize(object : AbstractYouTubePlayerListener(){
            override fun onReady(yTubePlayer: YouTubePlayer) {
                youTubePlayer = yTubePlayer
                val videoId = intent.getStringExtra(VIDEO_ID) ?: run { finish(); return }
                yTubePlayer.loadVideo(videoId, 0f)
            }
        }, iFrameOptions)

        lifecycle.addObserver(playerView)

        val formattedString = "${YouTubeUtility.formatViewCount(intent.getStringExtra(VIDEO_VIEW_COUNT).toString())} views"

        binding.textViewVideoTitle.text = intent.getStringExtra(VIDEO_TITLE)
        binding.textViewPublishDate.text =
            YouTubeUtility.formatDate(intent.getStringExtra(VIDEO_PUBLISH_DATE).toString())
        binding.textViewTotalViews.text = formattedString
        binding.textViewAuthor.text = intent.getStringExtra(VIDEO_AUTHOR)

        val channelId = intent.getStringExtra(CHANNEL_ID)
        if (channelId != null) {
            ApiConfig.getService()
                .getChannel("snippet,statistics", channelId)
                .enqueue(object : Callback<ChannelResponse> {
                    override fun onResponse(
                        call: Call<ChannelResponse?>,
                        response: Response<ChannelResponse?>
                    ) {
                        if (response.isSuccessful) {
                            val item = response.body()?.items?.firstOrNull() ?: return
                            val profilePic = item.snippet.thumbnails.high.url
                            Glide.with(this@PlayerActivity).load(profilePic).circleCrop()
                                .into(binding.channelProfilePicture)
                            val formattedString =
                                "${YouTubeUtility.formatViewCount(item.statistics.subscriberCount.toString())} subs"
                            binding.textViewSubscribers.text = formattedString
                        }
                    }
                    override fun onFailure(
                        call: Call<ChannelResponse?>,
                        t: Throwable
                    ) {}
                })
        }
    }

    companion object {
        const val VIDEO_ID = "video_id"
        const val VIDEO_TITLE = "video_title"
        const val VIDEO_PUBLISH_DATE = "video_published_at"
        const val VIDEO_AUTHOR = "video_author"
        const val VIDEO_VIEW_COUNT = "video_view_count"
        const val CHANNEL_ID = "channel_id"
    }

}