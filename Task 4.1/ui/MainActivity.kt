package com.trevin.ticknow.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import com.trevin.ticknow.R
import com.trevin.ticknow.data.model.TaskList
import com.trevin.ticknow.databinding.ActivityMainBinding
import com.trevin.ticknow.databinding.DialogAddTaskBinding
import com.trevin.ticknow.databinding.DialogAddTaskListBinding
import com.trevin.ticknow.databinding.TabButtonBinding
import com.trevin.ticknow.ui.tasks.StarredTasksFragment
import com.trevin.ticknow.ui.tasks.TasksFragment
import com.trevin.ticknow.util.InputValidator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private var currentTaskLists: List<TaskList> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).apply {
            lifecycleScope.launch {
                viewModel.getTaskLists().collectLatest { taskLists ->
                    currentTaskLists = taskLists
                    pager.adapter = SwipePageAdapter(this@MainActivity, taskLists)
                    pager.currentItem = 1
                    TabLayoutMediator(tabs, pager) { tab, position ->
                        when (position) {
                            0 -> tab.icon = ContextCompat.getDrawable(
                                this@MainActivity,
                                R.drawable.ic_star_filled
                            )

                            taskLists.size + 1 -> {
                                val buttonBinding = TabButtonBinding.inflate(layoutInflater)
                                tab.customView = buttonBinding.root.apply {
                                    setOnClickListener {
                                        showAddTaskListDialog()
                                    }
                                }
                            }

                            else -> {
                                tab.text = taskLists[position - 1].name
                                tab.view.setOnLongClickListener {
                                    showDeleteListDialog(taskLists[position - 1])
                                    true
                                }
                            }
                        }
                    }.attach()
                }
            }
            fab.imageTintList = ContextCompat.getColorStateList(this@MainActivity, R.color.PhotoBlue)
            fab.setOnClickListener {
                showAddTaskDialog()
            }
            setContentView(root)
        }
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun showDeleteListDialog(list: TaskList) {
        val customView: View = layoutInflater.inflate(R.layout.dialog_delete_list, null)
        val button: Button = customView.findViewById(R.id.dialog_button)

        val alertDialog = AlertDialog.Builder(this@MainActivity, R.style.CustomDialogStyle)
            .setView(customView)
            .create()

        button.setOnClickListener {
            viewModel.deleteTaskList(list)
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

    private fun showAddTaskListDialog() {
        val dialogBinding = DialogAddTaskListBinding.inflate(layoutInflater)
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.button_add_new_list))
            .setView(dialogBinding.root)
            .setPositiveButton("Create") { dialog, _ ->
                viewModel.addNewTaskList(dialogBinding.editTextListName.text?.toString())
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun showAddTaskDialog() {
        DialogAddTaskBinding.inflate(layoutInflater).apply {
            val dialog = BottomSheetDialog(this@MainActivity)
            dialog.setContentView(root)

            buttonSaveTask.isEnabled = false
            editTextTaskTitle.addTextChangedListener { input ->
                buttonSaveTask.isEnabled = InputValidator.isValidInput(input)
            }

            buttonShowDetails.setOnClickListener {
                editTextTaskDetails.visibility =
                    if (editTextTaskDetails.isVisible) View.GONE else View.VISIBLE
            }

            buttonSaveTask.setOnClickListener {
                viewModel.createTask(
                    title = editTextTaskTitle.text.toString(),
                    description = editTextTaskDetails.text.toString(),
                    listId = currentTaskLists[binding.pager.currentItem - 1].id
                )
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    inner class SwipePageAdapter(
        activity: FragmentActivity,
        private val taskLists: List<TaskList>
    ) :
        FragmentStateAdapter(activity) {

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> StarredTasksFragment()
                taskLists.size + 1 -> Fragment()
                else -> TasksFragment(taskLists[position - 1].id)
            }
        }

        override fun getItemCount() = taskLists.size + 2
    }
}