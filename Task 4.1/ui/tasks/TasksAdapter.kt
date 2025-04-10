package com.trevin.ticknow.ui.tasks

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.trevin.ticknow.data.model.Task
import com.trevin.ticknow.databinding.ItemTaskBinding
import java.text.SimpleDateFormat
import java.util.Locale

class TasksAdapter(private val listener: TaskItemClickListener) :
    RecyclerView.Adapter<TasksAdapter.ViewHolder>() {

    private var tasks: List<Task> = listOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ItemTaskBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(tasks[position])
    }

    fun setTasks(tasks: List<Task>) {
        this.tasks = tasks.sortedBy {
            it.isComplete
        }
        notifyDataSetChanged()
    }

    override fun getItemCount() = tasks.size

    inner class ViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            binding.apply {
                this.root.setOnClickListener {
                    listener.onTaskClicked(task)
                }

                checkBox.isChecked = task.isComplete
                toggleStar.isChecked = task.isStarred

                if (task.isComplete) {
                    textViewTitle.paintFlags =
                        textViewTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    textViewDetails.paintFlags =
                        textViewTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    textViewTitle.paintFlags = 0
                    textViewDetails.paintFlags = 0
                }

                textViewTitle.text = task.title

                if (task.description.isNullOrEmpty()){
                    textViewDetails.visibility = View.GONE
                } else {
                    textViewDetails.text = task.description
                    textViewDetails.visibility = View.VISIBLE
                }

                if (task.dueDate == null){
                    textViewDate.visibility = View.GONE
                } else {
                    textViewDate.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(task.dueDate)
                    textViewDate.visibility = View.VISIBLE
                }

                checkBox.setOnClickListener {
                    val updatedTask = task.copy(isComplete = checkBox.isChecked)
                    listener.onTaskUpdated(updatedTask)
                }
                toggleStar.setOnClickListener {
                    val updatedTask = task.copy(isStarred = toggleStar.isChecked)
                    listener.onTaskUpdated(updatedTask)
                }
            }
        }
    }

    // Interfaces allow for communication between a recycler view and a fragment or activity
    interface TaskItemClickListener {
        fun onTaskUpdated(task: Task)
        fun onTaskClicked(task: Task)
    }
}