package com.davorgotal.gotal_smartplanner.diagram

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.davorgotal.gotal_smartplanner.model.Quote
import com.davorgotal.gotal_smartplanner.databinding.FragmentDiagramBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import java.util.ArrayList


class DiagramFragment : Fragment() {
    private lateinit var binding: FragmentDiagramBinding
    val repository = DiagramRepositoryImpl()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDiagramBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository.getRandomQuote(object : QuoteListener {
            override fun getQuote(randomQuote: Quote) {
                binding.quoteTv.text = randomQuote.text
                binding.authorTv.text = randomQuote.author
            }

            override fun handleError() {
                Toast.makeText(context, "Error Occured", Toast.LENGTH_SHORT).show()
            }
        })
        repository.calculateTaskPercentageTime(object: PieChartListener{
            override fun getSum(value: Float) {
                setPieChart(value)
            }
        })
    }

    private fun setPieChart(suma: Float) {
        val pieChart = binding.pieChart
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(1440f - suma, "FREE TIME"))
        entries.add(PieEntry(suma, "WORK TIME"))

        val dataSet = PieDataSet(entries, "Categories")
        val colors = ArrayList<Int>()
        colors.add(Color.DKGRAY)
        colors.add(Color.rgb(0, 147, 202))
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

}
