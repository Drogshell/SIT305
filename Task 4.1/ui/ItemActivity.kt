package com.trevin.ticknow.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.datepicker.MaterialDatePicker
import com.trevin.ticknow.R
import com.trevin.ticknow.data.model.Task
import com.trevin.ticknow.databinding.ActivityItemBinding
import com.trevin.ticknow.ui.tasks.TasksViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityItemBinding
    private val viewModel: TasksViewModel by viewModels()

    private var taskId: Int = -1
    private var taskListID: Int = 0
    private var originalTitle: String? = null
    private var originalDetails: String? = null
    private var taskIsComplete: Boolean = false
    private var taskIsStarred: Boolean = false
    private var selectedDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left, systemBars.top, systemBars.right, systemBars.bottom
            )
            insets
        }
        intent?.let {
            taskId = it.getIntExtra("TASK_ID", -1)
            taskListID = it.getIntExtra("TASK_LIST_ID", 0)
            originalTitle = it.getStringExtra("TASK_TITLE")
            originalDetails = it.getStringExtra("TASK_DETAILS")
            taskIsComplete = it.getBooleanExtra("TASK_IS_COMPLETE", false)
            taskIsStarred = it.getBooleanExtra("TASK_IS_STARRED", false)
            selectedDate = it.getStringExtra("TASK_DATE")
        }

        setupUi()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                saveTaskChanges()
                finish()
            }
        })

    }

    private fun setupUi() {
        binding.itemEditTitle.setText(originalTitle)
        binding.itemEditDescription.setText(originalDetails)
        binding.starToggle.isChecked = taskIsStarred
        binding.editTextDate.setText(selectedDate)

        binding.editTextDate.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select A Date")
                .apply {
                    selectedDate?.let {
                        try {
                            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            val date: Date = formatter.parse(it)!!
                            setSelection(date.time)
                        } catch (e: Exception){
                            // Nothing happens
                        }
                    }
                }.build()

            datePicker.addOnPositiveButtonClickListener { selection ->
                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                selectedDate = formatter.format(Date(selection))
                binding.editTextDate.setText(selectedDate)
            }
            datePicker.show(supportFragmentManager, "MATERIAL_DATE_PICKER")
        }

        if (taskIsComplete) {
            binding.buttonMarkCompleted.text = "Mark As Incomplete"
            binding.buttonMarkCompleted.setOnClickListener {
                taskIsComplete = false
                saveTaskChanges()
                finish()
            }
        } else {
            binding.buttonMarkCompleted.setOnClickListener {
                taskIsComplete = true
                saveTaskChanges()
                finish()
            }
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Edit Task"

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val menuClickHandled = when (item.itemId) {
            R.id.delete_menu_item -> {
                deleteTask()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
        return menuClickHandled
    }

    private fun deleteTask() {
        if (taskId != -1) {
            val taskToDelete = Task(
                taskID = taskId,
                title = binding.itemEditTitle.text.toString().trim(),
                description = binding.itemEditDescription.text.toString().trim(),
                isComplete = taskIsComplete,
                isStarred = binding.starToggle.isChecked,
                listID = taskListID,
            )
            viewModel.deleteTask(taskToDelete)
        }
        finish()
    }

    private fun saveTaskChanges() {
        if (taskId != -1) {
            val dueDateLong: Long? = if (!selectedDate.isNullOrEmpty()){
                try {
                    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    formatter.parse(selectedDate)?.time
                } catch (e: Exception){
                    null
                }
            } else null

            val taskToUpdate = Task(
                taskID = taskId,
                title = binding.itemEditTitle.text.toString().trim(),
                description = binding.itemEditDescription.text.toString().trim(),
                isComplete = taskIsComplete,
                isStarred = binding.starToggle.isChecked,
                dueDate = dueDateLong,
                listID = taskListID,
            )
            viewModel.updateTask(taskToUpdate)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

}