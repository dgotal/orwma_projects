package com.davorgotal.gotal_smartplanner

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentTransaction
import com.davorgotal.gotal_smartplanner.databinding.FragmentNewTaskBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class NewTaskFragment : Fragment() {
    private lateinit var binding: FragmentNewTaskBinding
    private val db = Firebase.firestore

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        binding = FragmentNewTaskBinding.inflate(layoutInflater)
        val taskText = arguments?.getString("task")
        val taskDate = arguments?.getString("date")

        fun hideKeyboard() {
            val view = activity?.currentFocus
            if (view != null) {
                val imm =
                    activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
        binding.addTaskTitleEd.setText(taskText)
        binding.dateEd.setText(taskDate)

        fun addTask() {
            if(binding.startTime.text.isNullOrEmpty() || binding.dateEd.text.isNullOrEmpty() ||
                binding.endTime.text.isNullOrEmpty() || binding.addTaskTitleEd.text.isNullOrEmpty())
            {
                Toast.makeText(context, "Please input all required fields.", Toast.LENGTH_SHORT).show()
                return
            }
            val task = Task()
            task.startTime = binding.startTime.text.toString()
            task.endTime = binding.endTime.text.toString()
            task.description = binding.edAddDescription.text.toString()
            task.date = binding.dateEd.text.toString()
            task.title = binding.addTaskTitleEd.text.toString()
            db.collection("tasks")
                .add(task)
            val nextFragment = TasksFragment()
            val fragmentTransaction: FragmentTransaction? =
                activity?.supportFragmentManager?.beginTransaction()
            fragmentTransaction?.replace(R.id.frame_layout, nextFragment)
            fragmentTransaction?.commit()
        }

        fun cancelTask() {
            val nextFragment = TasksFragment()
            val fragmentTransaction: FragmentTransaction? =
                activity?.supportFragmentManager?.beginTransaction()
            fragmentTransaction?.replace(R.id.frame_layout, nextFragment)
            fragmentTransaction?.commit()
        }

        fun addDate() {
            binding.dateEd.setText(FirebaseDateFormatter.datePickerToString(binding.datePicker))
            binding.addDateView.visibility = View.GONE
        }

        binding.addTaskTitleEd.setOnEditorActionListener { _, actionId, event ->
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
        fun cancelDate() {
            binding.addDateView.visibility = View.GONE
        }
        binding.AddDateNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.add_date -> addDate()
                R.id.cancel_date -> cancelDate()
                else -> {
                }
            }
            true
        }
        binding.AddCancleNavigationBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.add_item -> addTask()
                R.id.cancle_item -> cancelTask()
                else -> {
                }
            }
            true
        }

        binding.dateEd.setOnClickListener {
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
            binding.addStartTimeFrame.visibility = View.GONE
            if (binding.AddEndTimeFrame.visibility == View.GONE) {
                binding.AddEndTimeFrame.visibility = View.VISIBLE

            } else {
                binding.AddEndTimeFrame.visibility = View.GONE
            }
        }
        fun addStartTime() {
            binding.startTime.setText(FirebaseDateFormatter.timePickerToString(binding.addStartTimeEd2))
            binding.addStartTimeFrame.visibility = View.GONE
        }

        fun cancelStartTime() {
            binding.addStartTimeFrame.visibility = View.GONE
        }

        fun addEndTime() {
            binding.endTime.setText(FirebaseDateFormatter.timePickerToString(binding.addEndTimeEd2))
            binding.AddEndTimeFrame.visibility = View.GONE
        }

        fun cancelEndTime() {
            binding.AddEndTimeFrame.visibility = View.GONE
        }
        binding.StartTimeNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.add_start_time -> addStartTime()
                R.id.cancle_start_time -> cancelStartTime()
                else -> {
                }
            }
            true
        }
        binding.EndTimeNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.add_end_time -> addEndTime()
                R.id.cancle_end_time -> cancelEndTime()
                else -> {
                }
            }
            true
        }
        return binding.root
    }

}
