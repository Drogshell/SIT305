package com.trevin.ticknow.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevin.ticknow.TickNowApplication
import com.trevin.ticknow.data.TaskRepository
import com.trevin.ticknow.data.model.Task
import com.trevin.ticknow.data.model.TaskList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.Date

class MainViewModel : ViewModel() {

    private val repository: TaskRepository = TickNowApplication.taskRepository

    fun createTask(title: String, description: String?, date: Date? = null, listId: Int){
        val task = Task(
            title = title,
            description = description,
            dueDate = date?.time,
            listID = listId
        )
        viewModelScope.launch{
            repository.createTask(task)
        }
    }

    fun getTaskLists(): Flow<List<TaskList>> = repository.getTaskLists()

    fun addNewTaskList(listName: String?) {
        if (listName == null) return
        viewModelScope.launch {
            repository.createTaskList(listName)
        }
    }

    fun deleteTaskList(taskList: TaskList){
        viewModelScope.launch {
            repository.deleteTaskList(taskList)
        }
    }
}