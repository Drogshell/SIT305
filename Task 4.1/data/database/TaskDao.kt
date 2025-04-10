package com.trevin.ticknow.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.trevin.ticknow.data.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert
    suspend fun createTask(task: Task)

    @Query("SELECT * FROM Task WHERE task.list_id = :taskListID")
    fun getAllTasks(taskListID: Int): Flow<List<Task>>

    @Query("SELECT * FROM Task WHERE task.isStarred = 1")
    fun getStarredTasks(): Flow<List<Task>>

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

}