package com.example.prog7312part2ui;

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.CheckBox
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DashboardActivity : AppCompatActivity() {

    private lateinit var calendarGrid: GridLayout
    private lateinit var monthYearText: TextView
    private lateinit var currentCalendar: Calendar
    private lateinit var emptyStateContainer: LinearLayout
    private lateinit var tasksScrollContainer: View
    private lateinit var completedTasksContainer: LinearLayout
    private lateinit var pendingTasksContainer: LinearLayout
    private var selectedDay = 2 // Default to day 2 (as shown in mockup)
    private val tasks = mutableListOf<Task>()

    data class Task(
        val id: String,
        val title: String,
        val isCompleted: Boolean = false,
        val subTasks: List<String> = emptyList(),
        val className: String = "",
        val location: String = "",
        val isAllDay: Boolean = false,
        val isRecurring: Boolean = false
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        initializeViews()
        setupCalendar()
        setupTodoListeners()
        updateTasksDisplay()

        // Setup navbar - update the reference to bottom_navbar
        val navbar = findViewById<View>(R.id.navigation_bar)
        NavbarHelper.setupNavbar(this, NavbarPage.DASHBOARD)
    }

    private fun initializeViews() {
        calendarGrid = findViewById(R.id.calendar_grid)
        monthYearText = findViewById(R.id.calendar_month_year)
        emptyStateContainer = findViewById(R.id.empty_state_container)
        tasksScrollContainer = findViewById(R.id.tasks_scroll_container)
        completedTasksContainer = findViewById(R.id.completed_tasks_container)
        pendingTasksContainer = findViewById(R.id.pending_tasks_container)

        val prevMonth = findViewById<ImageButton>(R.id.btn_prev_month)
        val nextMonth = findViewById<ImageButton>(R.id.btn_next_month)
        val menuButton = findViewById<ImageButton>(R.id.menu_button)

        // Set up month navigation
        prevMonth.setOnClickListener {
            currentCalendar.add(Calendar.MONTH, -1)
            updateCalendar()
        }

        nextMonth.setOnClickListener {
            currentCalendar.add(Calendar.MONTH, 1)
            updateCalendar()
        }

        // Menu button click
        menuButton.setOnClickListener {
            // Handle menu click - you can implement drawer or menu here
        }

        currentCalendar = Calendar.getInstance()
        // Set to August 2025 as shown in mockup
        currentCalendar.set(2025, Calendar.AUGUST, 1)
    }

    private fun setupCalendar() {
        updateCalendar()
    }

    private fun updateCalendar() {
        // Clear existing calendar days
        calendarGrid.removeAllViews()

        // Update month/year display
        val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        monthYearText.text = monthFormat.format(currentCalendar.time)

        // Get first day of month and number of days
        val tempCalendar = currentCalendar.clone() as Calendar
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK) - 1 // 0-based
        val daysInMonth = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        // Add empty cells for days before month starts
        for (i in 0 until firstDayOfWeek) {
            addEmptyDay()
        }

        // Add days of the month
        for (day in 1..daysInMonth) {
            addCalendarDay(day)
        }
    }

    private fun addEmptyDay() {
        val dayView = TextView(this)
        val params = GridLayout.LayoutParams().apply {
            width = 0
            height = GridLayout.LayoutParams.WRAP_CONTENT
            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            setMargins(4, 4, 4, 4)
        }
        dayView.layoutParams = params
        dayView.gravity = Gravity.CENTER
        calendarGrid.addView(dayView)
    }

    private fun addCalendarDay(day: Int) {
        val dayView = TextView(this).apply {
            text = day.toString()
            setTextColor(Color.parseColor("#D4AF37")) // Golden yellow for dark background
            textSize = 16f
            gravity = Gravity.CENTER
            setPadding(12, 12, 12, 12)
        }

        // Set layout parameters
        val params = GridLayout.LayoutParams().apply {
            width = 0
            height = 80
            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            setMargins(4, 4, 4, 4)
        }
        dayView.layoutParams = params

        // Highlight selected day (day 2 in mockup)
        if (day == selectedDay) {
            dayView.background = ContextCompat.getDrawable(this, R.drawable.calendar_day_selected)
            dayView.setTextColor(Color.BLACK) // Black text on golden selected background
        } else {
            dayView.background = ContextCompat.getDrawable(this, R.drawable.calendar_day_normal)
        }

        // Set click listener for day selection
        dayView.setOnClickListener {
            selectedDay = day
            updateCalendar() // Refresh to show new selection
        }

        calendarGrid.addView(dayView)
    }

    private fun setupTodoListeners() {
        // Add task button functionality
        findViewById<ImageButton>(R.id.btn_add_task)?.setOnClickListener {
            showAddTaskDialog()
        }
    }

    private fun showAddTaskDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add New Task")

        val input = android.widget.EditText(this)
        input.hint = "Enter task name"
        builder.setView(input)

        builder.setPositiveButton("Add") { _, _ ->
            val taskTitle = input.text.toString().trim()
            if (taskTitle.isNotEmpty()) {
                val newTask = Task(
                    id = System.currentTimeMillis().toString(),
                    title = taskTitle,
                    isCompleted = false
                )
                tasks.add(newTask)
                updateTasksDisplay()
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun updateTasksDisplay() {
        if (tasks.isEmpty()) {
            // Show empty state
            emptyStateContainer.visibility = View.VISIBLE
            tasksScrollContainer.visibility = View.GONE
        } else {
            // Show tasks
            emptyStateContainer.visibility = View.GONE
            tasksScrollContainer.visibility = View.VISIBLE

            // Clear existing task views
            completedTasksContainer.removeAllViews()
            pendingTasksContainer.removeAllViews()

            // Add tasks to appropriate containers
            tasks.forEach { task ->
                if (task.isCompleted) {
                    addCompletedTaskView(task)
                } else {
                    addPendingTaskView(task)
                }
            }
        }
    }

    private fun addCompletedTaskView(task: Task) {
        val taskLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            background = ContextCompat.getDrawable(this@DashboardActivity, R.drawable.completed_task_background)
            setPadding(16, 12, 16, 12)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 8
            }
        }

        // Animated checkmark
        val tickView = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(24, 24)
            setImageDrawable(ContextCompat.getDrawable(this@DashboardActivity, R.drawable.animated_checkmark))
            contentDescription = "Completed"
        }

        // Task title
        val titleView = TextView(this).apply {
            text = task.title
            setTextColor(Color.WHITE)
            textSize = 16f
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                marginStart = 12
            }
        }

        taskLayout.addView(tickView)
        taskLayout.addView(titleView)
        completedTasksContainer.addView(taskLayout)
    }

    private fun addPendingTaskView(task: Task) {
        val taskLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            background = ContextCompat.getDrawable(this@DashboardActivity, R.drawable.pending_task_background)
            setPadding(16, 12, 16, 12)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 8
            }
        }

        // Checkbox
        val checkbox = CheckBox(this).apply {
            buttonTintList = ContextCompat.getColorStateList(this@DashboardActivity, android.R.color.holo_orange_dark)
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    // Mark task as completed
                    val taskIndex = tasks.indexOfFirst { it.id == task.id }
                    if (taskIndex != -1) {
                        tasks[taskIndex] = task.copy(isCompleted = true)
                        updateTasksDisplay()

                        // Show completion animation (you can enhance this)
                        showTaskCompletedMessage("${task.title} completed!")
                    }
                }
            }
        }

        // Task title
        val titleView = TextView(this).apply {
            text = task.title
            setTextColor(Color.parseColor("#4A4A4A"))
            textSize = 16f
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                marginStart = 12
            }
        }

        taskLayout.addView(checkbox)
        taskLayout.addView(titleView)
        pendingTasksContainer.addView(taskLayout)
    }

    private fun showTaskCompletedMessage(message: String) {
        // Simple feedback - you can enhance with animations
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }
}