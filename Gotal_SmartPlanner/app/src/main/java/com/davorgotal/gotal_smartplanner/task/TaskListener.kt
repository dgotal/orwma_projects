package com.davorgotal.gotal_smartplanner.task

import com.davorgotal.gotal_smartplanner.model.RecyclerTask

interface TaskListener {
    fun setTasks(tasks: MutableList<RecyclerTask>) {}
    fun onSuccess() {}
}
