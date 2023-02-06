package com.davorgotal.gotal_smartplanner.task

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.davorgotal.gotal_smartplanner.FirebaseDateFormatter
import com.davorgotal.gotal_smartplanner.R
import com.davorgotal.gotal_smartplanner.databinding.FragmentNewTaskBinding
import com.davorgotal.gotal_smartplanner.model.Task

class NewTaskFragment : Fragment() {
    private lateinit var binding: FragmentNewTaskBinding
    private val taskRepository = TaskRepositoryImpl()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        binding = FragmentNewTaskBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val taskText = arguments?.getString("task")
        val taskDate = arguments?.getString("date")

        binding.etTaskTitle.setText(taskText)
        binding.etDate.setText(taskDate)

        binding.etTaskTitle.setOnEditorActionListener { _, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
            }
            false
        }
        binding.edAddDescription.setOnEditorActionListener { _, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
            }
            false
        }

        binding.tvCancelDate.setOnClickListener{
            binding.addDateView.isVisible = false
        }
        binding.tvSaveDate.setOnClickListener {
            addDate()
        }

        binding.tvCancelTask.setOnClickListener {
            replaceFragment(TasksFragment())
        }
        binding.tvSaveTask.setOnClickListener {
            addTask()
        }

        binding.etDate.setOnClickListener {
            hideKeyboard()
            if (binding.addDateView.visibility == View.GONE) {
                binding.addDateView.visibility = View.VISIBLE
            } else {
                binding.addDateView.visibility = View.GONE
            }

        }
        binding.startTime.setOnClickListener {
            hideKeyboard()
            binding.AddEndTimeFrame.visibility = View.GONE
            if (binding.addStartTimeFrame.visibility == View.GONE) {
                binding.addStartTimeFrame.visibility = View.VISIBLE
                binding.AddEndTimeFrame.visibility = View.GONE
            } else {
                binding.addStartTimeFrame.visibility = View.GONE
            }
        }

        binding.endTime.setOnClickListener {
            hideKeyboard()
            binding.addStartTimeFrame.isVisible = false
            if (binding.AddEndTimeFrame.visibility == View.GONE) {
                binding.AddEndTimeFrame.visibility = View.VISIBLE

            } else {
                binding.AddEndTimeFrame.isVisible = false
            }
        }

        binding.tvSaveStartTime.setOnClickListener {
            addStartTime()
        }
        binding.tvCancelStartTime.setOnClickListener {
            binding.addStartTimeFrame.isVisible = false
        }

        binding.tvSaveEndTime.setOnClickListener {
            addEndTime()
        }
        binding.tvCancelEndTime.setOnClickListener {
            binding.AddEndTimeFrame.isVisible = false
        }
    }

    fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction: FragmentTransaction? =
            activity?.supportFragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.frame_layout, fragment)
        fragmentTransaction?.commit()
    }

    private fun addStartTime() {
        binding.startTime.setText(FirebaseDateFormatter.timePickerToString(binding.addStartTimeEd2))
        binding.addStartTimeFrame.isVisible = false
    }

    private fun addEndTime() {
        binding.endTime.setText(FirebaseDateFormatter.timePickerToString(binding.addEndTimeEd2))
        binding.AddEndTimeFrame.isVisible = false
    }

    private fun addTask() {
        if (binding.startTime.text.isNullOrEmpty() || binding.etDate.text.isNullOrEmpty() ||
            binding.endTime.text.isNullOrEmpty() || binding.etTaskTitle.text.isNullOrEmpty()
        ) {
            Toast.makeText(context, "Please input all required fields.", Toast.LENGTH_SHORT).show()
            return
        }
        val task = Task()
        task.startTime = binding.startTime.text.toString()
        task.endTime = binding.endTime.text.toString()
        task.description = binding.edAddDescription.text.toString()
        task.date = binding.etDate.text.toString()
        task.title = binding.etTaskTitle.text.toString()
        taskRepository.addTask(task, object : TaskListener {
            override fun onSuccess() {
                super.onSuccess()
                replaceFragment(TasksFragment())
            }
        })
    }

    private fun addDate() {
        binding.etDate.setText(FirebaseDateFormatter.datePickerToString(binding.datePicker))
        binding.addDateView.isVisible = false
    }

    private fun hideKeyboard() {
        val view = activity?.currentFocus
        if (view != null) {
            val imm =
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}
