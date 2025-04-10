package com.trevin.ticknow.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = TaskList::class,
        parentColumns = ["task_list_id"],
        childColumns = ["list_id"],
        onDelete = ForeignKey.CASCADE // Also deletes any tasks associated
    )]
)
data class Task(
    @PrimaryKey(autoGenerate = true) val taskID: Int = 0,
    val title: String,
    val description: String? = null,
    val isStarred: Boolean = false,
    val isComplete: Boolean = false,
    val dueDate: Long? = null,
    @ColumnInfo(name = "list_id") val listID: Int
)