package com.davorgotal.gotal_smartplanner

import com.davorgotal.gotal_smartplanner.model.Task
import com.davorgotal.gotal_smartplanner.task.ItemClickType

interface ContentListener {
    fun onItemButtonClick(
        index: Int, task: Task, clickType: ItemClickType
    )
}
