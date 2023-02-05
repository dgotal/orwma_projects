package com.davorgotal.gotal_smartplanner

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.davorgotal.gotal_smartplanner.databinding.FragmentDiagramBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class DiagramFragment : Fragment() {
    private val db = Firebase.firestore
    private lateinit var binding: FragmentDiagramBinding
    private fun getRandomQuote() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://type.fit")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.getRandomQuote()

        call.enqueue(object : Callback<List<Quote>> {
            override fun onResponse(call: Call<List<Quote>>, response: Response<List<Quote>>) {
                val quotes = response.body()
                val randomIndex = (0 until quotes!!.size).random()
                val randomQuote = quotes[randomIndex]
                binding.quoteTv.text = randomQuote.text
                binding.authorTv.text = randomQuote.author
            }

            override fun onFailure(call: Call<List<Quote>>, t: Throwable) {
                Toast.makeText(activity, "Error Occured", Toast.LENGTH_SHORT).show()
            }
        })
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDiagramBinding.inflate(layoutInflater)

        fun calculateTimeSpan(startTime: String?, endTime: String?): Int {
            if (endTime != null && startTime != null) {
                val end = FirebaseDateFormatter.timeToMinutes(endTime)
                val start = FirebaseDateFormatter.timeToMinutes(startTime)
                return end - start
            } else {
                return 0
            }
        }

        fun isToday(date: Date?): Boolean {
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

        var counter = 0

        fun setPieChart(suma: Float) {
            val pieChart = binding.pieChart
            val entries = ArrayList<PieEntry>()
            entries.add(PieEntry(1440f - suma, "FREE TIME"))
            entries.add(PieEntry(suma, "WORK TIME"))

            val dataSet = PieDataSet(entries, "Categories")
            val colors = ArrayList<Int>()
            colors.add(Color.DKGRAY)
            colors.add(Color.rgb(0,147,202))
            dataSet.colors = colors

            val data = PieData(dataSet)
            pieChart.data = data
            pieChart.description.text = ""
            pieChart.setUsePercentValues(true)
            pieChart.animateXY(2000, 2000)
            pieChart.isDrawHoleEnabled = true
            pieChart.legend.isEnabled = false
            pieChart.invalidate()
            binding.tvFreeTime.setText("You are busy: " + (suma / 1440 * 100).toInt() + "% of day.")
        }

        fun calculateTaskPercentageTime() {
            db.collection("tasks").get()
                .addOnSuccessListener {
                    for (data in it.documents) {
                        val task = data.toObject(Task::class.java)
                        if (isToday(FirebaseDateFormatter.stringToDate(task?.date))) {
                            task?.id = data.id
                            counter += calculateTimeSpan(task?.startTime, task?.endTime)
                        }
                    }

                    setPieChart(counter.toFloat())
                }
                .addOnFailureListener {
                    Log.e("Task exception", it.message.toString())
                }
        }
        calculateTaskPercentageTime()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getRandomQuote()
    }

}
