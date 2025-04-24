package com.trevin.myplaylists.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.trevin.myplaylists.data.model.PlaylistVideoData
import com.trevin.myplaylists.databinding.ItemPlaylistVideoBinding
import com.trevin.myplaylists.util.YouTubeUtility

class PlaylistVideoAdapter(private val onClick: (PlaylistVideoData) -> Unit) : RecyclerView.Adapter<PlaylistVideoAdapter.Holder>() {

    private val items = mutableListOf<PlaylistVideoData>()

    fun submitList(newList: List<PlaylistVideoData>){
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {
        val binding = ItemPlaylistVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(
        holder: Holder,
        position: Int
    ) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class Holder(private val binding: ItemPlaylistVideoBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: PlaylistVideoData){
            binding.textViewVideoTitle.text = data.title
            binding.textViewAuthor.text = data.author
            val formattedString = "${YouTubeUtility.formatViewCount(data.viewCount)} views"
            binding.textViewPublishDate.text = YouTubeUtility.formatDate(data.publishedAt)
            binding.textViewTotalViews.text = formattedString
            Glide.with(binding.root).load(data.thumbnailUrl).into(binding.imageViewThumbnail)
            binding.root.setOnClickListener { onClick(data) }
        }
    }

}