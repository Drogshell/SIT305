package com.trevin.ticknow.ui.tasks

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.trevin.ticknow.data.model.Task
import com.trevin.ticknow.databinding.FragmentTasksBinding
import com.trevin.ticknow.ui.ItemActivity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TasksFragment(private val taskListID: Int) : Fragment(), TasksAdapter.TaskItemClickListener {

    private val viewModel: TasksViewModel by viewModels()
    private lateinit var binding: FragmentTasksBinding
    private val adapter = TasksAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.adapter = adapter
        fetchAllTasks()
    }

    private fun fetchAllTasks() {
        lifecycleScope.launch {
            viewModel.fetchTasks(taskListID).collect { tasks ->
                adapter.setTasks(tasks)
            }
        }
    }

    override fun onTaskUpdated(task: Task) {
        viewModel.updateTask(task)
    }

    override fun onTaskClicked(task: Task) {
        val dateString = task.dueDate?.let {
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
        }
        val intent = Intent(requireContext(), ItemActivity::class.java).apply {
            putExtra("TASK_ID", task.taskID)
            putExtra("TASK_TITLE", task.title)
            putExtra("TASK_DETAILS", task.description)
            putExtra("TASK_IS_COMPLETE", task.isComplete)
            putExtra("TASK_IS_STARRED", task.isStarred)
            putExtra("TASK_DATE", dateString)
            putExtra("TASK_LIST_ID", task.listID)
        }
        startActivity(intent)
    }

}