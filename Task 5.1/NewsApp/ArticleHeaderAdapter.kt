package com.trevin.newsapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.trevin.newsapp.data.NewsArticle
import com.trevin.newsapp.databinding.FragmentArticleHeaderBinding

class ArticleHeaderAdapter(
    private val article: NewsArticle
) : RecyclerView.Adapter<ArticleHeaderAdapter.HeaderVH>() {

    inner class HeaderVH(val binding: FragmentArticleHeaderBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        HeaderVH(
            FragmentArticleHeaderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: HeaderVH, position: Int) {
        with(holder.binding) {
            imageView.setImageResource(article.image)
            textViewTitle.text   = article.title.substringBefore(':')
            textViewAuthor.text  = article.author
            textViewHeading.text = article.title.substringAfter(':')
            textViewContent.text = article.content
        }
    }

    override fun getItemCount() = 1
}

