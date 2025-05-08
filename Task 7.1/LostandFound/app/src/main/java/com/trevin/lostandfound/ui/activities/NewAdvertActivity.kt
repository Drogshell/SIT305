package com.trevin.lostandfound.ui.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.trevin.lostandfound.R
import com.trevin.lostandfound.data.model.AdvertItem
import com.trevin.lostandfound.data.model.AdvertItemViewModel
import com.trevin.lostandfound.databinding.ActivityNewAdvertBinding
import com.trevin.lostandfound.util.Validation
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NewAdvertActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewAdvertBinding
    private val viewModel: AdvertItemViewModel by viewModels()

    private var selectedDate: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewAdvertBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.editTextTitle.addTextChangedListener { validTitle() }
        binding.editTextDescription.addTextChangedListener { validDescription() }
        binding.editTextDate.addTextChangedListener { validDate() }
        binding.editTextPhone.addTextChangedListener { editable ->
            val phoneText = editable.toString()
            if (!Validation.isValidPhoneNumber(phoneText)){
                binding.editTextPhoneLayout.error = "Invalid Number"
            } else {
                binding.editTextPhoneLayout.error = null
            }
        }

        binding.editTextDate.setOnClickListener {
            showDatePicker()
        }

        binding.buttonSave.setOnClickListener {
            if (validTitle() && validDescription() && validDate()) {
                saveAdvert()
            }
        }
    }

    private fun saveAdvert() {
        val rawPhoneNum = binding.editTextPhone.text.toString().trim()
        val formattedNum = if (rawPhoneNum.isNotEmpty() && Validation.isValidPhoneNumber(rawPhoneNum)){
            Validation.formatPhoneNumber(rawPhoneNum)
        } else {
            ""
        }

        val advertToCreate = AdvertItem(
            postType = if (binding.radioButtonLost.isChecked) "Lost" else "Found",
            title = binding.editTextTitle.text.toString().trim(),
            phoneNumber = formattedNum,
            description = binding.editTextDescription.text.toString().trim(),
            date = selectedDate!!,
            location = binding.editTextLocation.text.toString(),
            latitude = -1.0,
            longitude = -1.0
        )
        viewModel.createAdvert(advertToCreate)
        finish()
    }

    private fun validTitle(): Boolean {
        var valid = true

        val title = binding.editTextTitle.text.toString().trim()

        if (title.isEmpty()) {
            binding.editTextTitleLayout.error = "You must enter a title!"
            valid = false
        } else {
            binding.editTextTitleLayout.error = null
        }

        binding.buttonSave.isEnabled = valid
        return valid
    }

    private fun validDescription(): Boolean {
        var valid = true
        val description = binding.editTextDescription.text.toString().trim()
        if (description.isEmpty()) {
            binding.editTextDescriptionLayout.error = "Tell people about what was lost"
            valid = false
        } else if (description.length > 400) {
            binding.editTextDescriptionLayout.error = "Too many words!"
            valid = false
        } else {
            binding.editTextDescriptionLayout.error = null
        }
        binding.buttonSave.isEnabled = valid
        return valid
    }

    private fun validDate(): Boolean {
        var valid = true
        val date = binding.editTextDate.text.toString().trim()
        if (date.isEmpty()) {
            binding.editTextDateLayout.error = "When did you lose it?"
            valid = false
        } else {
            binding.editTextDateLayout.error = null
        }
        binding.buttonSave.isEnabled = valid
        return valid
    }

    private fun showDatePicker() {
        val today = MaterialDatePicker.todayInUtcMilliseconds()

        val constraintsBuilder = CalendarConstraints.Builder()
            .setEnd(today)
            .setValidator(DateValidatorPointBackward.now())

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Pick a date")
            .setSelection(today)
            .setCalendarConstraints(constraintsBuilder.build())
            .build()

        datePicker.show(supportFragmentManager, "MATERIAL_DATE_PICKER")

        datePicker.addOnPositiveButtonClickListener { selection ->
            selectedDate = Date(selection)
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            binding.editTextDate.setText(sdf.format(selectedDate))
        }
    }

}
