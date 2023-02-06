package com.davorgotal.gotal_smartplanner.task

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.davorgotal.gotal_smartplanner.ContentListener
import com.davorgotal.gotal_smartplanner.R
import com.davorgotal.gotal_smartplanner.databinding.FragmentTasksBinding
import com.davorgotal.gotal_smartplanner.model.RecyclerTask
import com.davorgotal.gotal_smartplanner.model.Task

class TasksFragment : Fragment(), ContentListener {
    private lateinit var binding: FragmentTasksBinding
    private lateinit var recyclerAdapter: TaskAdapter
    private val taskRepository = TaskRepositoryImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recyclerAdapter = TaskAdapter(mutableListOf(), this)
        val taskListener = object : TaskListener {
            override fun setTasks(tasks: MutableList<RecyclerTask>) {
                recyclerAdapter.setTasks(tasks)
            }
        }
        taskRepository.getTasks(taskListener)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTasksBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUI()
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
                    Toast.makeText(context, "Successfully deleted task.", Toast.LENGTH_SHORT).show()
                    recyclerAdapter.removeItem(index)
                }
            })
        }
    }

    private fun setUI() {
        binding.tasksList.layoutManager = LinearLayoutManager(context)
        binding.tasksList.adapter = recyclerAdapter
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
    }
}
