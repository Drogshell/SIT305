package com.trevin.ticknow.data

import com.trevin.ticknow.data.database.TaskDao
import com.trevin.ticknow.data.database.TaskListDao
import com.trevin.ticknow.data.model.Task
import com.trevin.ticknow.data.model.TaskList
import kotlinx.coroutines.flow.Flow

/**
 * Abstracts the UI layer and the data layer.
 * Now if I want to change the way data is stored, I just need to swap the taskDao.
 */
class TaskRepository(private val taskDao: TaskDao, private val taskListDao: TaskListDao) {

    suspend fun createTask(task: Task){
        taskDao.createTask(task)
    }

    fun getAllTasks(taskListID: Int): Flow<List<Task>> {
        return taskDao.getAllTasks(taskListID)
    }

    suspend fun updateTask(task: Task){
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: Task){
        taskDao.deleteTask(task)
    }

    fun getStarredTasks(): Flow<List<Task>> {
        return taskDao.getStarredTasks()
    }

    fun getTaskLists(): Flow<List<TaskList>>{
        return taskListDao.getAllTaskLists()
    }

    suspend fun createTaskList(listName: String) {
        val taskList = TaskList(name = listName)
        taskListDao.createTaskList(taskList)
    }

    suspend fun deleteTaskList(taskList: TaskList){
        taskListDao.deleteTaskList(taskList)
    }

}