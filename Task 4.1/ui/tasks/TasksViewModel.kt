package com.trevin.ticknow.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevin.ticknow.TickNowApplication
import com.trevin.ticknow.data.TaskRepository
import com.trevin.ticknow.data.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TasksViewModel : ViewModel() {

    private val repository: TaskRepository = TickNowApplication.Companion.taskRepository

    fun fetchTasks(taskListID: Int): Flow<List<Task>> {
        return repository.getAllTasks(taskListID)
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }
}