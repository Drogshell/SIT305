package com.trevin.lostandfound.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.trevin.lostandfound.R
import com.trevin.lostandfound.data.model.AdvertItem
import com.trevin.lostandfound.databinding.ItemAdvertBinding
import java.text.SimpleDateFormat
import java.util.Locale

class AdvertAdapter(private val listener: AdvertItemClickListener) :
    RecyclerView.Adapter<AdvertAdapter.ViewHolder>() {

    private var adverts: List<AdvertItem> = listOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ItemAdvertBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(adverts[position])
    }

    override fun getItemCount(): Int = adverts.size

    fun setAdverts(adverts: List<AdvertItem>) {
        this.adverts = adverts.sortedByDescending {
            it.date
        }
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemAdvertBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(advert: AdvertItem) {
            binding.apply {
                this.root.setOnClickListener {
                    listener.onAdvertClicked(advert)
                }

                if (advert.postType.equals("lost", ignoreCase = true)) {
                    textViewPostType.text = advert.postType
                    textViewPostType.setTextColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.md_theme_secondary
                        )
                    )
                    materialDivider.dividerColor =
                        ContextCompat.getColor(binding.root.context, R.color.md_theme_secondary)
                } else if (advert.postType.equals("found", ignoreCase = true)) {
                    textViewPostType.text = advert.postType
                    textViewPostType.setTextColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.colorCustomColor2
                        )
                    )
                    materialDivider.dividerColor =
                        ContextCompat.getColor(binding.root.context, R.color.colorCustomColor2)
                }

                textViewTitle.text = advert.title
                textViewDescription.text = advert.description
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                textViewDate.text = advert.date?.let { sdf.format(it) } ?: ""
            }
        }
    }

    interface AdvertItemClickListener {
        fun onAdvertClicked(advert: AdvertItem)
    }

}