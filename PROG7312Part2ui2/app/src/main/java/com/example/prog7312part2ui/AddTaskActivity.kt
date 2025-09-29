package com.example.prog7312part2ui

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddTaskActivity : AppCompatActivity() {

    private lateinit var taskTitleInput: EditText
    private lateinit var classInput: EditText
    private lateinit var locationInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var allDaySwitch: Switch
    private lateinit var recurringSwitch: Switch
    private lateinit var startTimeText: TextView
    private lateinit var endTimeText: TextView
    private lateinit var timeContainer: View
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button

    private var startTimeCalendar = Calendar.getInstance()
    private var endTimeCalendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_tasks)

        initializeViews()
        setupClickListeners()
        setupInitialTimes()

        // Setup navbar
        NavbarHelper.setupNavbar(this, NavbarPage.DASHBOARD)
    }

    private fun initializeViews() {
        taskTitleInput = findViewById(R.id.task_title_input)
        classInput = findViewById(R.id.class_input)
        locationInput = findViewById(R.id.location_input)
        descriptionInput = findViewById(R.id.description_input)
        allDaySwitch = findViewById(R.id.all_day_switch)
        recurringSwitch = findViewById(R.id.recurring_switch)
        startTimeText = findViewById(R.id.start_time_text)
        endTimeText = findViewById(R.id.end_time_text)
        timeContainer = findViewById(R.id.time_container)
        saveButton = findViewById(R.id.save_button)
        cancelButton = findViewById(R.id.cancel_button)

        val menuButton = findViewById<ImageButton>(R.id.menu_button)

        // Menu button click
        menuButton.setOnClickListener {
            // Handle menu click
        }
    }

    private fun setupClickListeners() {
        // All Day switch listener
        allDaySwitch.setOnCheckedChangeListener { _, isChecked ->
            timeContainer.visibility = if (isChecked) View.GONE else View.VISIBLE
        }

        // Start time picker
        startTimeText.setOnClickListener {
            showTimePicker(true)
        }

        // End time picker
        endTimeText.setOnClickListener {
            showTimePicker(false)
        }

        // Save button
        saveButton.setOnClickListener {
            saveTask()
        }

        // Cancel button
        cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun setupInitialTimes() {
        // Set default start time (7:00 AM)
        startTimeCalendar.set(Calendar.HOUR_OF_DAY, 7)
        startTimeCalendar.set(Calendar.MINUTE, 0)

        // Set default end time (8:00 PM / 20:00)
        endTimeCalendar.set(Calendar.HOUR_OF_DAY, 20)
        endTimeCalendar.set(Calendar.MINUTE, 0)

        updateTimeDisplays()
    }

    private fun showTimePicker(isStartTime: Boolean) {
        val calendar = if (isStartTime) startTimeCalendar else endTimeCalendar
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                calendar.set(Calendar.MINUTE, selectedMinute)
                updateTimeDisplays()
            },
            hour,
            minute,
            false // Use 12-hour format
        )

        timePickerDialog.show()
    }

    private fun updateTimeDisplays() {
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        startTimeText.text = timeFormat.format(startTimeCalendar.time)
        endTimeText.text = timeFormat.format(endTimeCalendar.time)
    }

    private fun saveTask() {
        val title = taskTitleInput.text.toString().trim()

        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter a task title", Toast.LENGTH_SHORT).show()
            return
        }

        // Create task data
        val taskData = TaskData(
            title = title,
            className = classInput.text.toString().trim(),
            location = locationInput.text.toString().trim(),
            description = descriptionInput.text.toString().trim(),
            isAllDay = allDaySwitch.isChecked,
            startTime = if (!allDaySwitch.isChecked) startTimeCalendar.time else null,
            endTime = if (!allDaySwitch.isChecked) endTimeCalendar.time else null,
            isRecurring = recurringSwitch.isChecked
        )

        // Pass task data back to dashboard
        val resultIntent = Intent().apply {
            putExtra("TASK_TITLE", taskData.title)
            putExtra("TASK_CLASS", taskData.className)
            putExtra("TASK_LOCATION", taskData.location)
            putExtra("TASK_DESCRIPTION", taskData.description)
            putExtra("TASK_ALL_DAY", taskData.isAllDay)
            putExtra("TASK_RECURRING", taskData.isRecurring)
            if (!taskData.isAllDay) {
                putExtra("TASK_START_TIME", taskData.startTime?.time ?: 0L)
                putExtra("TASK_END_TIME", taskData.endTime?.time ?: 0L)
            }
        }

        setResult(RESULT_OK, resultIntent)
        finish()

        Toast.makeText(this, "Task saved successfully!", Toast.LENGTH_SHORT).show()
    }

    data class TaskData(
        val title: String,
        val className: String,
        val location: String,
        val description: String,
        val isAllDay: Boolean,
        val startTime: Date?,
        val endTime: Date?,
        val isRecurring: Boolean
    )
}