package com.trevin.ticknow

import android.app.Application
import com.trevin.ticknow.data.TaskRepository
import com.trevin.ticknow.data.database.TickNowDatabase

class TickNowApplication : Application() {

    // One time application global set up
    override fun onCreate() {
        super.onCreate()
        val database = TickNowDatabase.getDatabase(this)
        val taskDao = database.getTaskDao()
        val taskListDao = database.getTaskListDao()
        taskRepository = TaskRepository(taskDao, taskListDao)
    }

    companion object {
        lateinit var taskRepository: TaskRepository
    }

}