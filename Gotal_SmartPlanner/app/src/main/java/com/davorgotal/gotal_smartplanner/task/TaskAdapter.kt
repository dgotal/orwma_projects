package com.davorgotal.gotal_smartplanner.task

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.davorgotal.gotal_smartplanner.ContentListener
import com.davorgotal.gotal_smartplanner.databinding.RecyclerDateItemBinding
import com.davorgotal.gotal_smartplanner.databinding.RecyclerItemBinding
import com.davorgotal.gotal_smartplanner.model.RecyclerTask
import com.davorgotal.gotal_smartplanner.model.Task

enum class ItemClickType {
    EDIT,
    REMOVE,
}

class TaskAdapter(
    private val tasks: MutableList<RecyclerTask>, private val listener: ContentListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> {

                TaskViewHolder(
                    RecyclerDateItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> {
                PlaceHolderViewHolder(
                    RecyclerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TaskViewHolder -> {
                holder.bind(position, tasks[position]as Task, listener)
            }
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (tasks[position]) {
            is Task -> {
                0
            }
            else -> {
                1
            }
        }
    }

    fun removeItem(index: Int) {
        tasks.removeAt(index)
        notifyItemRemoved(index)
        notifyItemRangeChanged(index, tasks.size)
    }

    fun addItem(task: RecyclerTask) {
        tasks.add(0, task)
        notifyItemInserted(0)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setTasks(newTasks: MutableList<RecyclerTask>) {
        tasks.clear()
        tasks.addAll(newTasks)
        notifyDataSetChanged()
    }

    class TaskViewHolder(private val binding: RecyclerDateItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            index: Int,
            task: Task,
            listener: ContentListener
        ) {

            binding.tvTitle.setText(task.title)
            binding.tvDescription.setText(task.description)
            binding.edStartTime.setText(task.startTime.toString())
            binding.edEndTime.setText(task.endTime.toString())

           binding.bttnEdit.setOnClickListener {
                task.title = binding.tvTitle.text.toString()
                task.description = binding.tvDescription.text.toString()
                task.endTime =
                    binding.edEndTime.text.toString()
                task.startTime =
                    binding.edStartTime.text.toString()

                listener.onItemButtonClick(index, task, ItemClickType.EDIT)
            }

            binding.bttnDelete.setOnClickListener {
                listener.onItemButtonClick(index, task, ItemClickType.REMOVE)
            }

        }
    }
    class PlaceHolderViewHolder(binding: RecyclerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }
}
