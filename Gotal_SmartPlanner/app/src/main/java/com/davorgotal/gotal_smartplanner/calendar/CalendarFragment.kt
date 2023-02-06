package com.davorgotal.gotal_smartplanner.calendar

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.davorgotal.gotal_smartplanner.*
import com.davorgotal.gotal_smartplanner.databinding.FragmentCalendarBinding
import com.davorgotal.gotal_smartplanner.model.RecyclerTask
import com.davorgotal.gotal_smartplanner.model.Task
import com.davorgotal.gotal_smartplanner.task.*
import java.util.*


class CalendarFragment : Fragment(), ContentListener {
    private lateinit var binding: FragmentCalendarBinding
    private lateinit var recyclerAdapter: TaskAdapter
    private val taskRepository = TaskRepositoryImpl()
    private val taskListener = object : TaskListener {
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUI()
    }

    private fun setUI() {
        binding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            taskRepository.getCalendarTasks(taskListener, "$dayOfMonth.${month + 1}.$year")
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
        binding.calendarRecyclerTasks.layoutManager = LinearLayoutManager(context)
        binding.calendarRecyclerTasks.adapter = recyclerAdapter

        taskRepository.getCalendarTasks(
            taskListener,
            FirebaseDateFormatter.calendarToString(Calendar.getInstance())
        )
    }

    override fun onItemButtonClick(index: Int, task: Task, clickType: ItemClickType) {
        Log.d("Main activity", clickType.toString())

        if (clickType == ItemClickType.EDIT) {
            taskRepository.editTask(task, object : TaskListener {
                override fun onSuccess() {
                    super.onSuccess()
                    Toast.makeText(context, "Successfully edited task.", Toast.LENGTH_SHORT).show()
                }
            })
        }
        if (clickType == ItemClickType.REMOVE) {
            taskRepository.removeTask(task.id, object : TaskListener {
                override fun onSuccess() {
                    super.onSuccess()
                    recyclerAdapter.removeItem(index)
                    Toast.makeText(context, "Successfully deleted task.", Toast.LENGTH_SHORT).show()

                }
            })
        }
    }
}
