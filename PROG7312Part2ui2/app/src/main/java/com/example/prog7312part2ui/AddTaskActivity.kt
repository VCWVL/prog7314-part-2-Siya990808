package com.example.prog7312part2ui

import android.app.AlertDialog
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
import com.example.prog7312part2ui.models.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
    private lateinit var menuComponent: MenuComponent  // Add this

    private var startTimeCalendar = Calendar.getInstance()
    private var endTimeCalendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_tasks)

        initializeViews()
        setupClickListeners()
        setupInitialTimes()
        setupMenuComponent()

        // navbar setup
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
        menuComponent = findViewById(R.id.menu_component)

        val menuButton = findViewById<ImageButton>(R.id.menu_button)

        // Menu button click - toggle menu
        menuButton.setOnClickListener {
            menuComponent.toggleMenu()
        }
    }

    private fun setupMenuComponent() {
        menuComponent.onMenuItemClickListener = { menuItem ->
            when (menuItem) {
                MenuComponent.MenuItem.PROFILE -> {
                    Toast.makeText(this, "Profile coming soon!", Toast.LENGTH_SHORT).show()
                }
                MenuComponent.MenuItem.BOOKSHOP_MARKET -> {
                    Toast.makeText(this, "Bookshop Market coming soon!", Toast.LENGTH_SHORT).show()
                }
                MenuComponent.MenuItem.CALENDAR -> {
                    // Go to Dashboard
                    val intent = Intent(this, DashboardActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    finish()
                }
                MenuComponent.MenuItem.CALCULATOR -> {
                    Toast.makeText(this, "Calculator coming soon!", Toast.LENGTH_SHORT).show()
                }
                MenuComponent.MenuItem.CLASSROOMS -> {
                    val intent = Intent(this, ClassesActivity::class.java)
                    startActivity(intent)
                }

                MenuComponent.MenuItem.STUDY_HALL -> {
                    Toast.makeText(this, "Study Hall coming soon!", Toast.LENGTH_SHORT).show()
                }
                MenuComponent.MenuItem.STUDYHIVE_NEWS -> {
                    Toast.makeText(this, "StudyHive News coming soon!", Toast.LENGTH_SHORT).show()
                }
                MenuComponent.MenuItem.SETTINGS -> {
                    Toast.makeText(this, "Settings coming soon!", Toast.LENGTH_SHORT).show()
                }
                MenuComponent.MenuItem.SIGN_OUT -> {
                    showSignOutConfirmation()
                }
            }
        }
    }

    private fun showSignOutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Sign Out")
            .setMessage("Are you sure you want to sign out?")
            .setPositiveButton("Yes") { _, _ ->
                val intent = Intent(this, SignInActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("No", null)
            .show()
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
        // default start time (7:00 AM)
        startTimeCalendar.set(Calendar.HOUR_OF_DAY, 7)
        startTimeCalendar.set(Calendar.MINUTE, 0)

        // default end time (8:00 PM / 20:00)
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
            false
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

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = currentUser.uid
        val db = FirebaseFirestore.getInstance()

        // Build task data
        val taskData = hashMapOf(
            "title" to title,
            "className" to classInput.text.toString().trim(),
            "location" to locationInput.text.toString().trim(),
            "description" to descriptionInput.text.toString().trim(),
            "isAllDay" to allDaySwitch.isChecked,
            "startTime" to if (!allDaySwitch.isChecked) startTimeCalendar.time else null,
            "endTime" to if (!allDaySwitch.isChecked) endTimeCalendar.time else null,
            "isRecurring" to recurringSwitch.isChecked,
            "createdAt" to System.currentTimeMillis()
        )

        // Save task under user → users/{uid}/tasks/{taskId}
        db.collection("users")
            .document(userId)
            .collection("tasks")
            .add(taskData)  // Auto-generates taskId
            .addOnSuccessListener {
                Toast.makeText(this, "Task saved successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving task: ${e.message}", Toast.LENGTH_SHORT).show()
            }
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