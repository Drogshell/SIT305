package com.trevin.myplaylists.ui

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.trevin.myplaylists.R
import com.trevin.myplaylists.data.PlaylistRepo
import com.trevin.myplaylists.data.PlaylistVideoAdapter
import com.trevin.myplaylists.data.database.PlaylistDatabase
import com.trevin.myplaylists.databinding.ActivityHomePageBinding
import com.trevin.myplaylists.databinding.BottomSheetDialogAddVideoBinding
import com.trevin.myplaylists.ui.viewModel.HomePageViewModel
import com.trevin.myplaylists.util.YouTubeUtility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isVisible
import com.trevin.myplaylists.network.ApiConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext


class HomePageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomePageBinding
    private lateinit var playlistRepo: PlaylistRepo
    private lateinit var viewModel: HomePageViewModel
    private var userID: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        userID = intent.getLongExtra("USER_ID", -1L)
        if (userID < 0) throw IllegalStateException("NO USER ID!")

        val database = PlaylistDatabase.getInstance(applicationContext)
        playlistRepo = PlaylistRepo.getInstance(database)

        viewModel = ViewModelProvider(
            this,
            HomePageViewModel.Factory(playlistRepo)
        )[HomePageViewModel::class.java]
        viewModel.loadVideosFor(userID)

        binding.extendedFab.setOnClickListener {
            showAddVideoSheet()
        }

        val adapter = PlaylistVideoAdapter { data ->
            startActivity(
                Intent(this, PlayerActivity::class.java)
                    .putExtra(PlayerActivity.VIDEO_ID, data.videoId)
                    .putExtra(PlayerActivity.VIDEO_TITLE, data.title)
                    .putExtra(PlayerActivity.VIDEO_PUBLISH_DATE, data.publishedAt)
                    .putExtra(PlayerActivity.VIDEO_AUTHOR, data.author)
                    .putExtra(PlayerActivity.VIDEO_VIEW_COUNT, data.viewCount)
                    .putExtra(PlayerActivity.CHANNEL_ID, data.channelId)
            )
        }

        binding.recyclerPlaylistView.adapter = adapter
        binding.recyclerPlaylistView.layoutManager = LinearLayoutManager(this)

        val swipeCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) {
                val pos = viewHolder.adapterPosition
                val toDelete = viewModel.videos.value[pos]
                viewModel.deletePlaylistItem(toDelete.playListItemId)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && dX != 0f) {
                    val itemView = viewHolder.itemView

                    val errorTint = ContextCompat.getColor(itemView.context, R.color.md_theme_error)
                    val backGround = ColorDrawable(errorTint)
                    val left = if (dX > 0) itemView.left else (itemView.right + dX).toInt()
                    val right = if (dX > 0) (itemView.left + dX).toInt() else itemView.right
                    backGround.setBounds(left, itemView.top, right, itemView.bottom)
                    backGround.draw(c)

                    val icon =
                        ContextCompat.getDrawable(this@HomePageActivity, R.drawable.ic_delete)!!
                    val iconHeight = icon.intrinsicHeight
                    val iconWidth = icon.intrinsicWidth
                    val iconMargin = (itemView.height - iconHeight) / 2

                    val iconTop = itemView.top + iconMargin
                    val iconBottom = iconTop + iconHeight

                    if (dX > 0) {
                        val iconLeft = itemView.left + (iconMargin / 2)
                        val iconRight = iconLeft + iconWidth
                        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    } else {
                        val iconRight = itemView.right - (iconMargin / 2)
                        val iconLeft = iconRight - iconWidth
                        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    }
                    icon.draw(c)
                }
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }

        ItemTouchHelper(swipeCallback).attachToRecyclerView(binding.recyclerPlaylistView)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLoading.combine(viewModel.videos) { loading, videos ->
                    loading to videos
                }.collect { (loading, videos) ->
                    binding.homePageLoader.isVisible = loading
                    binding.recyclerPlaylistView.isVisible = !loading
                    binding.extendedFab.isEnabled = !loading
                    binding.imageViewErrorSplash.isVisible = !loading && videos.isEmpty()
                    adapter.submitList(videos)
                }
            }
        }

        binding.recyclerPlaylistView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    binding.extendedFab.shrink()
                    binding.extendedFab.postDelayed({
                        binding.extendedFab.hide()
                    }, 300)
                } else if (dy < 0) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                        binding.extendedFab.show()
                        binding.extendedFab.postDelayed({
                            binding.extendedFab.extend()
                        }, 300)
                    }
                }
            }
        })
    }

    private fun showAddVideoSheet() {
        val sheet = BottomSheetDialogAddVideoBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(this).apply {
            setContentView(sheet.root)
            show()
        }

        fun getValidVideoId(): String? {
            val url = sheet.textInputEditYoutubeUrl.text.toString().trim()
            if (url.isEmpty()) {
                sheet.textInputLayoutYoutubeUrl.error = "Enter a URL!"
                return null
            }
            val vid = YouTubeUtility.extractYouTubeId(url)
            if (vid == null) {
                sheet.textInputLayoutYoutubeUrl.error = "Not a valid YouTube URL!"
                return null
            }
            sheet.textInputLayoutYoutubeUrl.error = null
            return vid
        }

        sheet.buttonAddToPlaylist.setOnClickListener {
            val video = getValidVideoId() ?: return@setOnClickListener

            lifecycleScope.launch(Dispatchers.IO) {
                playlistRepo.addURL(userID, sheet.textInputEditYoutubeUrl.text.toString().trim())
            }
            Snackbar.make(binding.recyclerPlaylistView, "Added to playlist", Snackbar.LENGTH_LONG)
                .show()
            dialog.dismiss()
        }

        sheet.buttonWatchNow.setOnClickListener {
            val video = getValidVideoId() ?: return@setOnClickListener

            lifecycleScope.launch(Dispatchers.IO) {
                val response = ApiConfig.getService().getVideoById(id = video).execute()
                val item = response.body()?.items?.firstOrNull() ?: return@launch
                withContext(Dispatchers.Main) {
                    startActivity(
                        Intent( this@HomePageActivity, PlayerActivity::class.java)
                            .putExtra(PlayerActivity.VIDEO_ID, video)
                            .putExtra(PlayerActivity.VIDEO_TITLE, item.snippet.title)
                            .putExtra(PlayerActivity.VIDEO_AUTHOR, item.snippet.channelTitle)
                            .putExtra(PlayerActivity.VIDEO_PUBLISH_DATE, item.snippet.publishedAt)
                            .putExtra(PlayerActivity.VIDEO_VIEW_COUNT, item.statistics.viewCount)
                            .putExtra(PlayerActivity.CHANNEL_ID, item.snippet.channelId)
                    )
                    dialog.dismiss()
                }
            }
        }
    }
}