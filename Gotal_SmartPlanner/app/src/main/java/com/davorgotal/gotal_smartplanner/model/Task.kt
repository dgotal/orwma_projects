package com.davorgotal.gotal_smartplanner.model

sealed class RecyclerTask
{
}

data class Task  (
    var id: String = "",
    var date: String? = null,
    var title: String? = null,
    var description: String? = null,
    var startTime : String? = null,
    var endTime: String? = null
) : RecyclerTask()

object PlaceHolderTask : RecyclerTask()
data class DateTask(
    var date: String? = null,
) : RecyclerTask()
