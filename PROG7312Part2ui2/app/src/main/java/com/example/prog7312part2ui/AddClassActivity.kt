package com.example.prog7312part2ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.example.prog7312part2ui.models.Classroom

class AddClassActivity : AppCompatActivity() {

    private lateinit var titleInput: EditText
    private lateinit var teacherInput: EditText
    private lateinit var locationInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var allYearSwitch: SwitchCompat
    private lateinit var recurringSwitch: SwitchCompat
    private lateinit var semesterContainer: LinearLayout
    private lateinit var firstSemesterBtn: TextView
    private lateinit var secondSemesterBtn: TextView
    private lateinit var attachTimetableBtn: LinearLayout
    private lateinit var saveBtn: FrameLayout
    private lateinit var cancelBtn: FrameLayout

    private var isFirstSemester = true // Default to first semester

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_class)

        // Initialize views
        titleInput = findViewById(R.id.titleInput)
        teacherInput = findViewById(R.id.teacherInput)
        locationInput = findViewById(R.id.locationInput)
        descriptionInput = findViewById(R.id.descriptionInput)
        allYearSwitch = findViewById(R.id.allYearSwitch)
        recurringSwitch = findViewById(R.id.recurringSwitch)
        semesterContainer = findViewById(R.id.semesterContainer)
        firstSemesterBtn = findViewById(R.id.firstSemesterBtn)
        secondSemesterBtn = findViewById(R.id.secondSemesterBtn)
        attachTimetableBtn = findViewById(R.id.attachTimetableBtn)
        saveBtn = findViewById(R.id.saveBtn)
        cancelBtn = findViewById(R.id.cancelBtn)

        // Set default state
        allYearSwitch.isChecked = true
        semesterContainer.visibility = View.GONE

        // All Year Switch listener
        allYearSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                semesterContainer.visibility = View.GONE
            } else {
                semesterContainer.visibility = View.VISIBLE
            }
        }

        // First Semester Button
        firstSemesterBtn.setOnClickListener {
            isFirstSemester = true
            firstSemesterBtn.setBackgroundResource(R.drawable.semester_btn_selected)
            secondSemesterBtn.setBackgroundResource(R.drawable.semester_btn_unselected)
        }

        // Second Semester Button
        secondSemesterBtn.setOnClickListener {
            isFirstSemester = false
            firstSemesterBtn.setBackgroundResource(R.drawable.semester_btn_unselected)
            secondSemesterBtn.setBackgroundResource(R.drawable.semester_btn_selected)
        }

        // Attach Timetable Button
        attachTimetableBtn.setOnClickListener {
            Toast.makeText(this, "Attach timetable functionality coming soon", Toast.LENGTH_SHORT).show()
        }

        // Save Button
        saveBtn.setOnClickListener {
            saveClass()
        }

        // Cancel Button
        cancelBtn.setOnClickListener {
            finish()
        }
    }

    private fun saveClass() {
        val title = titleInput.text.toString().trim()
        val teacher = teacherInput.text.toString().trim()
        val location = locationInput.text.toString().trim()
        val description = descriptionInput.text.toString().trim()
        val allYear = allYearSwitch.isChecked
        val recurring = recurringSwitch.isChecked

        // Validate
        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter a class title", Toast.LENGTH_SHORT).show()
            return
        }

        // Determine semester
        val semester = when {
            allYear -> "All Year"
            isFirstSemester -> "First Semester"
            else -> "Second Semester"
        }

        // Create Classroom object (don't format description here, let adapter handle it)
        val classroom = Classroom(
            title = title,
            teacher = teacher,
            location = location,
            description = description, // Store raw description
            isAllYear = allYear,
            isRecurring = recurring,
            semester = semester
        )

        val resultIntent = Intent()
        resultIntent.putExtra("NEW_CLASS", classroom)
        setResult(Activity.RESULT_OK, resultIntent)

        Toast.makeText(this, "Class added successfully!", Toast.LENGTH_SHORT).show()
        finish()
    }
}