package com.trevin.lostandfound.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.trevin.lostandfound.data.model.AdvertItem
import com.trevin.lostandfound.data.model.AdvertItemViewModel
import com.trevin.lostandfound.databinding.FragmentAdvertBinding
import com.trevin.lostandfound.ui.AdvertAdapter
import com.trevin.lostandfound.ui.activities.ExistingAdvertActivity
import com.trevin.lostandfound.ui.activities.MainActivity
import kotlinx.coroutines.launch

class AdvertFragment : Fragment(), AdvertAdapter.AdvertItemClickListener {

    private lateinit var binding: FragmentAdvertBinding
    private val adapter = AdvertAdapter(this)
    private val viewModel: AdvertItemViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdvertBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.adapter = adapter

        binding.recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                (activity as? MainActivity.ExtendedFabController)?.onRecyclerViewScrolled(dy)
            }
        })
        getAllAdverts()
    }

    private fun getAllAdverts() {
        lifecycleScope.launch {
            viewModel.getAdverts().collect { adverts ->
                adapter.setAdverts(adverts)
            }
        }
    }

    override fun onAdvertClicked(advert: AdvertItem) {
        val intent = Intent(requireContext(), ExistingAdvertActivity::class.java).apply {
            putExtra("ADVERT_ID", advert.id)
            putExtra("POST_TYPE", advert.postType)
            putExtra("TITLE", advert.title)
            putExtra("PHONE_NUMBER", advert.phoneNumber)
            putExtra("DESCRIPTION", advert.description)
            putExtra("DATE", advert.date)
            putExtra("LOCATION", advert.location)
            putExtra("LATITUDE", advert.latitude)
            putExtra("LONGITUDE", advert.longitude)
        }
        startActivity(intent)
    }

}