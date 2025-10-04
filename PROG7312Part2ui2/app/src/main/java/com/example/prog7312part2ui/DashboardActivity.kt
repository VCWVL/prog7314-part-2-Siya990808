package com.example.prog7312part2ui

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
import android.content.Intent
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.example.prog7312part2ui.models.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DashboardActivity : AppCompatActivity() {

    private lateinit var calendarGrid: GridLayout
    private lateinit var monthYearText: TextView
    private lateinit var currentCalendar: Calendar
    private lateinit var emptyStateContainer: LinearLayout
    private lateinit var tasksScrollContainer: View
    private lateinit var completedTasksContainer: LinearLayout
    private lateinit var pendingTasksContainer: LinearLayout
    private lateinit var menuComponent: MenuComponent

    private var selectedDay = 2 // Default to day 2
    private val tasks = mutableListOf<Task>()

    // Firebase
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val addTaskLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        initializeViews()
        setupCalendar()
        setupTodoListeners()
        setupMenu()
        setupBackPressHandler()

        // Load tasks from Firestore
        loadTasksFromFirestore()

        // Setup navbar
        NavbarHelper.setupNavbar(this, NavbarPage.DASHBOARD)
    }

    private fun initializeViews() {
        calendarGrid = findViewById(R.id.calendar_grid)
        monthYearText = findViewById(R.id.calendar_month_year)
        emptyStateContainer = findViewById(R.id.empty_state_container)
        tasksScrollContainer = findViewById(R.id.tasks_scroll_container)
        completedTasksContainer = findViewById(R.id.completed_tasks_container)
        pendingTasksContainer = findViewById(R.id.pending_tasks_container)
        menuComponent = findViewById(R.id.menu_component)

        val prevMonth = findViewById<ImageButton>(R.id.btn_prev_month)
        val nextMonth = findViewById<ImageButton>(R.id.btn_next_month)
        val menuButton = findViewById<ImageButton>(R.id.menu_button)

        prevMonth.setOnClickListener {
            currentCalendar.add(Calendar.MONTH, -1)
            updateCalendar()
        }

        nextMonth.setOnClickListener {
            currentCalendar.add(Calendar.MONTH, 1)
            updateCalendar()
        }

        menuButton.setOnClickListener {
            menuComponent.toggleMenu()
        }

        currentCalendar = Calendar.getInstance()
        currentCalendar.set(2025, Calendar.AUGUST, 1)
    }

    private fun setupMenu() {
        menuComponent.onMenuItemClickListener = { menuItem ->
            when (menuItem) {
                MenuComponent.MenuItem.PROFILE -> showToast("Profile clicked")
                MenuComponent.MenuItem.BOOKSHOP_MARKET -> showToast("Bookshop Market clicked")
                MenuComponent.MenuItem.CALENDAR -> showToast("Calendar clicked")
                MenuComponent.MenuItem.CALCULATOR -> {
                    val intent = Intent(this, CalculatorActivity::class.java)
                    startActivity(intent)
                }
                MenuComponent.MenuItem.CLASSROOMS -> {
                    val intent = Intent(this, ClassesActivity::class.java)
                    startActivity(intent)
                }

                MenuComponent.MenuItem.STUDY_HALL -> showToast("Study Hall clicked")
                MenuComponent.MenuItem.STUDYHIVE_NEWS -> {
                    val intent = Intent(this, NewsletterActivity::class.java)
                    startActivity(intent)
                }
                MenuComponent.MenuItem.SETTINGS -> showToast("Settings clicked")
                MenuComponent.MenuItem.SIGN_OUT -> handleSignOut()
            }
        }
    }

    private fun handleSignOut() {
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

    private fun setupCalendar() {
        updateCalendar()
    }

    private fun updateCalendar() {
        calendarGrid.removeAllViews()

        val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        monthYearText.text = monthFormat.format(currentCalendar.time)

        val tempCalendar = currentCalendar.clone() as Calendar
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK) - 1
        val daysInMonth = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        for (i in 0 until firstDayOfWeek) {
            addEmptyDay()
        }

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
            setTextColor(Color.parseColor("#D4AF37"))
            textSize = 16f
            gravity = Gravity.CENTER
            setPadding(12, 12, 12, 12)
        }

        val params = GridLayout.LayoutParams().apply {
            width = 0
            height = 80
            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            setMargins(4, 4, 4, 4)
        }
        dayView.layoutParams = params

        if (day == selectedDay) {
            dayView.background = ContextCompat.getDrawable(this, R.drawable.calendar_day_selected)
            dayView.setTextColor(Color.BLACK)
        } else {
            dayView.background = ContextCompat.getDrawable(this, R.drawable.calendar_day_normal)
        }

        dayView.setOnClickListener {
            selectedDay = day
            updateCalendar()
        }

        calendarGrid.addView(dayView)
    }

    private fun setupTodoListeners() {
        findViewById<ImageButton>(R.id.btn_add_task)?.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            addTaskLauncher.launch(intent)
        }
    }

    private fun loadTasksFromFirestore() {
        val currentUser = auth.currentUser ?: return

        db.collection("users")
            .document(currentUser.uid)
            .collection("tasks")
            .orderBy("createdAt")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    showToast("Failed to load tasks: ${e.message}")
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    tasks.clear()
                    for (doc in snapshots.documents) {
                        val task = doc.toObject(Task::class.java)?.copy(id = doc.id)
                        if (task != null) tasks.add(task)
                    }
                    updateTasksDisplay()
                }
            }
    }

    private fun updateTasksDisplay() {
        if (tasks.isEmpty()) {
            emptyStateContainer.visibility = View.VISIBLE
            tasksScrollContainer.visibility = View.GONE
        } else {
            emptyStateContainer.visibility = View.GONE
            tasksScrollContainer.visibility = View.VISIBLE

            completedTasksContainer.removeAllViews()
            pendingTasksContainer.removeAllViews()

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
            ).apply { bottomMargin = 8 }
        }

        val tickView = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(24, 24)
            setImageDrawable(ContextCompat.getDrawable(this@DashboardActivity, R.drawable.animated_checkmark))
            contentDescription = "Completed"
        }

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
            ).apply { bottomMargin = 8 }
        }

        val checkbox = CheckBox(this).apply {
            buttonTintList = ContextCompat.getColorStateList(this@DashboardActivity, android.R.color.holo_orange_dark)
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    val taskIndex = tasks.indexOfFirst { it.id == task.id }
                    if (taskIndex != -1) {
                        val updatedTask = task.copy(isCompleted = true)

                        // Update Firestore
                        val currentUser = auth.currentUser
                        if (currentUser != null) {
                            db.collection("users")
                                .document(currentUser.uid)
                                .collection("tasks")
                                .document(task.id)
                                .set(updatedTask)
                        }
                    }
                }
            }
        }

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

    private fun showToast(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun setupBackPressHandler() {
        onBackPressedDispatcher.addCallback(this) {
            if (menuComponent.isOpen()) {
                menuComponent.closeMenu()
            } else {
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }
}
