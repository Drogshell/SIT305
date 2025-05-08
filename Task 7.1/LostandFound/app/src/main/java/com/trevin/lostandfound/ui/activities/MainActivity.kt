package com.trevin.lostandfound.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.trevin.lostandfound.R
import com.trevin.lostandfound.databinding.ActivityMainBinding
import com.trevin.lostandfound.ui.fragments.AdvertFragment
import com.trevin.lostandfound.ui.fragments.MapFragment
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), MainActivity.ExtendedFabController {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).apply {
            lifecycleScope.launch {
                pager.adapter = SwipePageAdapter(this@MainActivity)
                pager.currentItem = 0
                TabLayoutMediator(tabLayout, pager) { tab, position ->
                    when(position){
                        0 -> tab.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_dashboard)
                        else -> tab.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_map_pin)
                    }
                }.attach()
            }
        }

        setContentView(binding.root)

        binding.fabAddNewAdvert.setOnClickListener {
            val intent = Intent(this, NewAdvertActivity::class.java)
            startActivity(intent)
        }



        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onRecyclerViewScrolled(dy: Int) {
        if (dy > 0 && binding.fabAddNewAdvert.isExtended){
            binding.fabAddNewAdvert.shrink()
        } else if (dy < 0 && !binding.fabAddNewAdvert.isExtended) {
            binding.fabAddNewAdvert.extend()
        }
    }

    inner class SwipePageAdapter( activity: FragmentActivity ) : FragmentStateAdapter(activity){
        override fun createFragment(position: Int): Fragment {
            return when(position){
                0 -> AdvertFragment()
                1 -> MapFragment()
                else -> throw IllegalArgumentException("Unexpected position")
            }
        }

        override fun getItemCount(): Int = 2

    }

    interface ExtendedFabController{
        fun onRecyclerViewScrolled(dy: Int)
    }

}