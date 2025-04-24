package com.trevin.newsapp

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.trevin.newsapp.data.NewsArticle
import com.trevin.newsapp.data.NewsRepo.articles
import com.trevin.newsapp.databinding.ActivityMainBinding

interface ArticleItemClickListener {
    fun onArticleClicked(article: NewsArticle)
}

class MainActivity : AppCompatActivity(), ArticleItemClickListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbarTopBar)

        binding.carouselLayout.apply {
            setHasFixedSize(true)
            layoutManager = CarouselLayoutManager()
            CarouselSnapHelper().attachToRecyclerView(this)
            adapter = CarouselAdapter(articles, this@MainActivity)
        }

        binding.newsFeedLayout.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            adapter = NewsFeedAdapter(articles, this@MainActivity)
        }

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0){
                binding.fragmentContainer.visibility = View.GONE
            }
        }

    }

    override fun onArticleClicked(article: NewsArticle) {
        binding.fragmentContainer.visibility = View.VISIBLE

        val fragment = ArticleFragment.newInstance(article)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
            .addToBackStack(null).commit()
    }
}