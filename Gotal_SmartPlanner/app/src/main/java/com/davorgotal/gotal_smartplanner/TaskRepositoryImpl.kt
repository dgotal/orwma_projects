package com.davorgotal.gotal_smartplanner

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList


class TaskRepositoryImpl {
    private val db = Firebase.firestore
    private fun isToday(date: Date?): Boolean {
        val calendar = Calendar.getInstance()
        val currentWeekStart = calendar.get(Calendar.WEEK_OF_YEAR)
        val currentYearStart = calendar.get(Calendar.YEAR)
        val currentDayStart = calendar.get(Calendar.DAY_OF_WEEK)
        if (date != null) {
            calendar.time = date
        }
        val targetWeek = calendar.get(Calendar.WEEK_OF_YEAR)
        val targetYear = calendar.get(Calendar.YEAR)
        val targetDay = calendar.get(Calendar.DAY_OF_WEEK)
        return currentWeekStart == targetWeek && currentYearStart == targetYear && currentDayStart == targetDay
    }
    fun getTasks(taskListener: TaskListener) {
        db.collection("tasks").get()
            .addOnSuccessListener {
                val taskList = ArrayList<RecyclerTask>()

                for (data in it.documents) {
                    val task = data.toObject(Task::class.java)
                    if (task != null && isToday(FirebaseDateFormatter.stringToDate(task.date))) {
                        task.id = data.id
                        taskList.add(task)
                    }
                }
                taskList.sortWith(compareBy { (it as? Task)?.startTime })
                taskListener.setTasks(taskList)
            }
            .addOnFailureListener {
                Log.e("Task exception", it.message.toString())
            }
    }
    fun calculateTimeSpan(startTime: String?, endTime: String?): Int {
        if (endTime != null && startTime != null) {
            val end = FirebaseDateFormatter.timeToMinutes(endTime)
            val start = FirebaseDateFormatter.timeToMinutes(startTime)
            return end - start
        } else {
            return 0
        }
    }

    fun sendNotification(date: String)
    {
        db.collection("tasks").get()
            .addOnSuccessListener {
                for(data in it.documents)
                {
                    val task = data.toObject(Task::class.java)
                    task?.id = data.id
                    if(task != null)
                    {
                        if(calculateTimeSpan(task.startTime, date) < 60)
                        {
                            //createNotification(context, task.title.toString(), "You have soon task.")
                        }
                    }

                }
            }
    }

    fun createNotification(context: Context, title: String, message: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                "channel_id",
                "channel_name",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.description = "channel_description"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val builder = NotificationCompat.Builder(context, "channel_id")
            .setSmallIcon(R.drawable.ic_baseline_delete_24)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notification = builder.build()
        notificationManager.notify(0, notification)
    }

    fun getCalendarTasks(taskListener: TaskListener, date: String){
        db.collection("tasks").get()
            .addOnSuccessListener {
                val taskList = ArrayList<RecyclerTask>()
                for (data in it.documents) {
                    val task = data.toObject(Task::class.java)
                            task?.id = data.id
                    if (task != null && task.date.toString() == date) {
                        taskList.add(task)
                    }
                }
                taskList.sortWith(compareBy({ (it as? DateTask)?.date },
                    { (it as? Task)?.startTime }))
                taskListener.setTasks(taskList)
            }
            .addOnFailureListener {
                Log.e("Task exception", it.message.toString())
            }
    }
}
