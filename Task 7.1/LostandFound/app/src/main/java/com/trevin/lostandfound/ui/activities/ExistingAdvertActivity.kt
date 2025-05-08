package com.trevin.lostandfound.ui.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.trevin.lostandfound.R
import com.trevin.lostandfound.data.model.AdvertItem
import com.trevin.lostandfound.data.model.AdvertItemViewModel
import com.trevin.lostandfound.databinding.ActivityExistingAdvertBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExistingAdvertActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExistingAdvertBinding
    private val viewModel: AdvertItemViewModel by viewModels()

    private var advertId = -1
    private var postType: String? = null
    private var title: String? = null
    private var phoneNumber: String? = null
    private var description: String? = null
    private var date: Date? = null
    private var location: String? = null
    private var latitude: Long? = null
    private var longitude: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExistingAdvertBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        intent?.let { originalAdvert ->
            advertId = originalAdvert.getIntExtra("ADVERT_ID", -1)
            postType = originalAdvert.getStringExtra("POST_TYPE")
            title = originalAdvert.getStringExtra("TITLE")
            phoneNumber = originalAdvert.getStringExtra("PHONE_NUMBER")
            description = originalAdvert.getStringExtra("DESCRIPTION")
            date = originalAdvert.getSerializableExtra("DATE") as? Date
            location = originalAdvert.getStringExtra("LOCATION")
            latitude = originalAdvert.getLongExtra("LATITUDE", -1)
            longitude = originalAdvert.getLongExtra("LONGITUDE", -1)
        }

        setUpFields()

        binding.buttonDelete.setOnClickListener {
            val advertToDelete = AdvertItem(
                id = advertId,
                postType = postType.toString(),
                title = title.toString(),
                phoneNumber = phoneNumber.toString(),
                description = description,
                date = date,
                location = location.toString(),
                latitude = latitude?.toDouble(),
                longitude = longitude?.toDouble(),
            )
            viewModel.deleteAdvert(advertToDelete)
            finish()
        }
    }

    private fun setUpFields() {
        if (postType.equals("lost", ignoreCase = true)) {
            binding.postTypeTextView.text = "LOST"
            binding.postTypeTextView.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.md_theme_secondary
                )
            )
        } else if (postType.equals("found", ignoreCase = true)) {
            binding.postTypeTextView.text = "FOUND"
            binding.postTypeTextView.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorCustomColor2
                )
            )
        }

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.timeTextView.text = date?.let { sdf.format(it) } ?: ""
        binding.titleTextView.text = title

        if (phoneNumber!!.isEmpty()) {
            binding.phoneTextView.text = "No Number Provided"
        } else {
            binding.phoneTextView.text = phoneNumber.toString()
        }

        binding.descriptionTextView.text = description

    }

}