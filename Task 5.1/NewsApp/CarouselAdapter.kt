package com.trevin.newsapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.trevin.newsapp.data.NewsArticle
import com.trevin.newsapp.databinding.CarouselItemBinding

class CarouselAdapter(private val articles: List<NewsArticle>, private val listener: ArticleItemClickListener) :
    RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CarouselViewHolder {
        return CarouselViewHolder(
            CarouselItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: CarouselViewHolder,
        position: Int
    ) {
        holder.bind(articles[position])
    }

    override fun getItemCount(): Int = articles.size

    inner class CarouselViewHolder(private val binding: CarouselItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(article: NewsArticle) {
            binding.root.setOnClickListener {
                listener.onArticleClicked(article)
            }
            Glide.with(binding.imageView).load(article.image).into(binding.imageView)
            binding.textViewTitle.text = article.title.substringBefore(':')
        }
    }

}