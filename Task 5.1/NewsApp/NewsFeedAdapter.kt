package com.trevin.newsapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.trevin.newsapp.data.NewsArticle
import com.trevin.newsapp.databinding.NewsFeedItemBinding

class NewsFeedAdapter(private val articles: List<NewsArticle>, private val listener: ArticleItemClickListener) :
    RecyclerView.Adapter<NewsFeedAdapter.FeedViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FeedViewHolder {
        return FeedViewHolder(
            NewsFeedItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: FeedViewHolder,
        position: Int
    ) {
        holder.bind(articles[position])
    }

    override fun getItemCount(): Int = articles.size

    inner class FeedViewHolder(private val binding: NewsFeedItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(article: NewsArticle) {
            binding.root.setOnClickListener {
                listener.onArticleClicked(article)
            }
            Glide.with(binding.imageView).load(article.image).into(binding.imageView)
            binding.textViewTitle.text = article.title.substringBefore(':')
            binding.textViewHeading.text = article.title.substringAfter(':')
            binding.textViewContent.text = article.content.replaceRange(100, article.content.length , "...")
        }
    }

}