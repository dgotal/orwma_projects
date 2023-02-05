package com.davorgotal.gotal_smartplanner

interface ContentListener {
    fun onItemButtonClick(
        index: Int, task: Task, clickType: ItemClickType
    )
}
