package com.davorgotal.gotal_smartplanner

import android.os.Build
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.util.*

object FirebaseDateFormatter {
    private val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy")

    fun dateToString(date: Date): String {
        return simpleDateFormat.format(date)
    }

    fun datePickerToString(date: DatePicker) : String{
        val dayofMonth = date.dayOfMonth
        val month = date.month
        val year = date.year
        return "$dayofMonth.${month+1}.$year"
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun timePickerToString(time: TimePicker) : String{
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, time.hour)
        calendar.set(Calendar.MINUTE, time.minute)

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return timeFormat.format(calendar.time)
    }

    fun stringToDate(string: String?): Date?
    {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return string?.let { dateFormat.parse(it) }
    }
    fun stringToTime(time: String?) : Date
    {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = time?.let { format.parse(it) }
        val calendar = Calendar.getInstance()
        if (date != null) {
            calendar.time = date
        }
        return calendar.time
    }
    fun timeToMinutes(time: String): Int {
        val parts = time.split(":")
        val hours = parts[0].toInt()
        val minutes = parts[1].toInt()
        return hours * 60 + minutes
    }
    fun calendarToString(calendar: Calendar) : String
    {
        val dateFormat = SimpleDateFormat("d.M.yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}
