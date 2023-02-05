package com.davorgotal.gotal_smartplanner

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.davorgotal.gotal_smartplanner.databinding.FragmentCalendarBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class CalendarFragment : Fragment(), ContentListener {
    private lateinit var binding: FragmentCalendarBinding
    private val db = Firebase.firestore
    private lateinit var recyclerAdapter: TaskAdapter
    val taskRepository = TaskRepositoryImpl()
    val taskListener = object : TaskListener {
        override fun setTasks(tasks: MutableList<RecyclerTask>) {
            recyclerAdapter.setTasks(tasks)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recyclerAdapter = TaskAdapter(mutableListOf(), this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalendarBinding.inflate(layoutInflater)
        binding.calendarView.setOnDateChangeListener{view, year, month, dayOfMonth ->
            var date = "$dayOfMonth.${month+1}.$year"

            taskRepository.getCalendarTasks(taskListener, date)
        }
        binding.insertTasks.setOnClickListener {
            val taskText = binding.bottomEditText.text.toString()

            val bundle = Bundle()
            bundle.putString("task", taskText)
            val nextFragment = NewTaskFragment()
            nextFragment.arguments = bundle
            val fragmentTransaction: FragmentTransaction? =
                activity?.supportFragmentManager?.beginTransaction()
            fragmentTransaction?.replace(R.id.frame_layout, nextFragment)
            fragmentTransaction?.commit()
        }
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.calendarRecyclerTasks.layoutManager = LinearLayoutManager(context)
        binding.calendarRecyclerTasks.adapter = recyclerAdapter
        var date = FirebaseDateFormatter.calendarToString(Calendar.getInstance())
        taskRepository.getCalendarTasks(taskListener, date)
    }

    override fun onItemButtonClick(index: Int, task: Task, clickType: ItemClickType) {
        Log.d("Main activity", clickType.toString())

        if (clickType == ItemClickType.EDIT) {
            db.collection("tasks").document(task.id).set(task)
            Toast.makeText(context, "Successfully edited task.", Toast.LENGTH_SHORT).show()
        }
        if (clickType == ItemClickType.REMOVE) {
            recyclerAdapter.removeItem(index)
            db.collection("tasks").document(task.id).delete()
            Toast.makeText(context, "Successfully deleted task.", Toast.LENGTH_SHORT).show()
        }
    }
}
