package com.trevin.newsapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.trevin.newsapp.data.NewsArticle
import com.trevin.newsapp.data.NewsRepo
import com.trevin.newsapp.databinding.FragmentArticleBinding

class ArticleFragment : Fragment() {

    private lateinit var binding: FragmentArticleBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentArticleBinding.inflate(inflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val article = arguments?.let { args ->
            NewsArticle(
                args.getInt(KEY_IMG),
                args.getString(KEY_TITLE).orEmpty(),
                args.getString(KEY_AUTHOR).orEmpty(),
                args.getString(KEY_CONTENT).orEmpty()
            )
        } ?: return

        val related = NewsRepo.articles.filter { it.title != article.title }

        val headerAdapter  = ArticleHeaderAdapter(article)
        val relatedAdapter = NewsFeedAdapter(related, activity as ArticleItemClickListener)

        val concat = ConcatAdapter(headerAdapter, relatedAdapter)

        binding.articleRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = concat
            setHasFixedSize(true)
        }

    }

    companion object {
        private const val KEY_IMG = "KEY_IMG"
        private const val KEY_TITLE = "KEY_TITLE"
        private const val KEY_AUTHOR = "KEY_AUTHOR"
        private const val KEY_CONTENT = "KEY_CONTENT"

        fun newInstance(article: NewsArticle) = ArticleFragment().apply {
            arguments = Bundle().apply {
                putInt(KEY_IMG, article.image)
                putString(KEY_TITLE, article.title)
                putString(KEY_AUTHOR, article.author)
                putString(KEY_CONTENT, article.content)
            }
        }

    }

}