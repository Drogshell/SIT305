package com.trevin.ticknow.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevin.ticknow.TickNowApplication
import com.trevin.ticknow.data.TaskRepository
import com.trevin.ticknow.data.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class StarredTasksViewModel : ViewModel() {

    private val repository: TaskRepository = TickNowApplication.Companion.taskRepository

    fun fetchTasks(): Flow<List<Task>> {
        return repository.getStarredTasks()
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }
}