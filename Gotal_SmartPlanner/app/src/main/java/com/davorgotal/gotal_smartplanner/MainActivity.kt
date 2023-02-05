package com.davorgotal.gotal_smartplanner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import androidx.fragment.app.Fragment
import com.davorgotal.gotal_smartplanner.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setUpActionBarAndBottomNav()
        setContentView(binding.root)
        setUpUI()
        replaceFragment(CalendarFragment())
    }
    private fun setUpActionBarAndBottomNav() {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.calendar_item -> replaceFragment(CalendarFragment())
                R.id.task_item -> replaceFragment(TasksFragment())
                R.id.diagram_item -> replaceFragment(DiagramFragment())
                else -> {
                }
            }
            true
        }
    }
    private fun setUpUI() {
        binding.currentDaytv.text = FirebaseDateFormatter.dateToString(Date())
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}
