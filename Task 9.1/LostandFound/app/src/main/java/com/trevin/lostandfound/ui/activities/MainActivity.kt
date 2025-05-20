package com.trevin.lostandfound.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.model.KeyPath
import com.airbnb.lottie.value.LottieValueCallback
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.trevin.lostandfound.R
import com.trevin.lostandfound.databinding.ActivityMainBinding
import com.trevin.lostandfound.ui.fragments.AdvertFragment
import com.trevin.lostandfound.ui.fragments.MapsFragment
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), MainActivity.ExtendedFabController {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dashboardTab: LottieAnimationView
    private lateinit var mapsTab: LottieAnimationView

    private val MARKER_IDLE = 59
    private val MENU_IDLE = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).apply {
            lifecycleScope.launch {
                pager.adapter = SwipePageAdapter(this@MainActivity)
                pager.currentItem = 0
                TabLayoutMediator(tabLayout, pager) { tab, position ->
                    when (position) {
                        0 -> tab.customView = createAnimationTabView(R.raw.menu, 0)
                        else -> tab.customView = createAnimationTabView(R.raw.marker, 1)
                    }
                }.attach()
            }
        }
        setContentView(binding.root)

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val lottieView = tab.customView as? LottieAnimationView ?: return
                val selectedColor =
                    ContextCompat.getColor(this@MainActivity, R.color.md_theme_primary)
                lottieView.addValueCallback(
                    KeyPath("**"),
                    LottieProperty.COLOR,
                    LottieValueCallback(selectedColor)
                )
                lottieView.addValueCallback(
                    KeyPath("**"),
                    LottieProperty.STROKE_COLOR,
                    LottieValueCallback(selectedColor)
                )

                lottieView.setMinAndMaxFrame(0, lottieView.maxFrame.toInt())
                lottieView.playAnimation()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                val lottieView = tab.customView as? LottieAnimationView ?: return
                val unselectedColor =
                    ContextCompat.getColor(this@MainActivity, R.color.md_theme_inverseSurface)
                lottieView.addValueCallback(
                    KeyPath("**"),
                    LottieProperty.COLOR,
                    LottieValueCallback(unselectedColor)
                )
                lottieView.addValueCallback(
                    KeyPath("**"),
                    LottieProperty.STROKE_COLOR,
                    LottieValueCallback(unselectedColor)
                )

                lottieView.cancelAnimation()
                setIdleFrame(tab.position, lottieView)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                val lottieView = tab.customView as? LottieAnimationView ?: return
                lottieView.setMinAndMaxFrame(0, lottieView.maxFrame.toInt())
                lottieView.playAnimation()
            }

            private fun setIdleFrame(position: Int, lottieView: LottieAnimationView) {
                when (position) {
                    0 -> lottieView.frame = MENU_IDLE
                    1 -> lottieView.frame = MARKER_IDLE
                }
            }

        })

        binding.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 0) {
                    binding.fabAddNewAdvert.show()
                    binding.fabAddNewAdvert.postDelayed({
                        binding.fabAddNewAdvert.extend()
                    }, 250L)
                } else {
                    binding.fabAddNewAdvert.shrink()
                    binding.fabAddNewAdvert.postDelayed({
                        binding.fabAddNewAdvert.hide()
                    }, 250L)
                }
            }
        })

        binding.fabAddNewAdvert.setOnClickListener {
            val intent = Intent(this, NewAdvertActivity::class.java)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                binding.fabAddNewAdvert,
                binding.fabAddNewAdvert.transitionName
            )
            startActivity(intent, options.toBundle())
        }

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }

    private fun createAnimationTabView(animRes: Int, position: Int): LottieAnimationView {
        val lottieView = LottieAnimationView(this)
        lottieView.setAnimation(animRes)
        lottieView.repeatCount = 0
        lottieView.layoutParams = ViewGroup.LayoutParams(100, 100)
        if (position == 0) {
            lottieView.frame = MENU_IDLE
            dashboardTab = lottieView
        } else {
            lottieView.frame = MARKER_IDLE
            mapsTab = lottieView
        }
        return lottieView
    }

    override fun onRecyclerViewScrolled(dy: Int) {
        if (dy > 0 && binding.fabAddNewAdvert.isShown && binding.fabAddNewAdvert.isExtended) {
            binding.fabAddNewAdvert.shrink()
            binding.fabAddNewAdvert.postDelayed({
                binding.fabAddNewAdvert.hide()
            }, 250L)
        } else if (dy < 0 && !binding.fabAddNewAdvert.isShown) {
            binding.fabAddNewAdvert.show()
            binding.fabAddNewAdvert.postDelayed({
                binding.fabAddNewAdvert.extend()
            }, 250L)
        }
    }

    inner class SwipePageAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> AdvertFragment()
                1 -> MapsFragment()
                else -> throw IllegalArgumentException("Unexpected position")
            }
        }

        override fun getItemCount(): Int = 2

    }

    interface ExtendedFabController {
        fun onRecyclerViewScrolled(dy: Int)
    }

}