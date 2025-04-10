package com.trevin.ticknow.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.trevin.ticknow.data.model.TaskList
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskListDao {

    @Insert
    suspend fun createTaskList(list: TaskList)

    @Query("SELECT * FROM TaskList")
    fun getAllTaskLists(): Flow<List<TaskList>>

    @Update
    suspend fun updateTaskList(list: TaskList)

    @Delete
    suspend fun deleteTaskList(list: TaskList)

}