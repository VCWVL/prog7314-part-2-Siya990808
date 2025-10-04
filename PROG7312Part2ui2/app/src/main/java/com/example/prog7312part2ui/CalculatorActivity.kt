package com.example.prog7312part2ui

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.prog7312part2ui.models.Classroom

class CalculatorActivity : AppCompatActivity() {

    private lateinit var classSpinner: Spinner
    private lateinit var addAssignmentBtn: TextView
    private lateinit var assignmentsContainer: LinearLayout
    private lateinit var calculateBtn: FrameLayout
    private lateinit var resultContainer: LinearLayout
    private lateinit var finalGradeText: TextView
    private lateinit var gradeLetterText: TextView
    private lateinit var tableHeader: LinearLayout

    private val classList = mutableListOf<Classroom>()
    private val assignments = mutableListOf<Assignment>()
    private var selectedClass: Classroom? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)

        // Initialize views
        classSpinner = findViewById(R.id.classSpinner)
        addAssignmentBtn = findViewById(R.id.addAssignmentBtn)
        assignmentsContainer = findViewById(R.id.assignmentsContainer)
        calculateBtn = findViewById(R.id.calculateBtn)
        resultContainer = findViewById(R.id.resultContainer)
        finalGradeText = findViewById(R.id.finalGradeText)
        gradeLetterText = findViewById(R.id.gradeLetterText)
        tableHeader = findViewById(R.id.tableHeader)

        // Load classes
        loadClasses()
        setupClassSpinner()

        // Add Assignment button click
        addAssignmentBtn.setOnClickListener {
            if (selectedClass != null) {
                showAddAssignmentDialog()
            } else {
                Toast.makeText(this, "Please select a class first", Toast.LENGTH_SHORT).show()
            }
        }

        // Calculate button click
        calculateBtn.setOnClickListener {
            calculateGrade()
        }

        // Setup navbar
        setupNavbar()
    }

    private fun loadClasses() {
        // Load classes from ClassDataManager or your data source
        classList.clear()
        classList.addAll(ClassDataManager.getClasses())
    }

    private fun setupClassSpinner() {
        val classNames = mutableListOf<String>()

        if (classList.isEmpty()) {
            classNames.add("Add Class +")
        } else {
            classNames.add("Select a class...")
            classList.forEach { classNames.add(it.title) }
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, classNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        classSpinner.adapter = adapter

        var isFirstSelection = true  // 👈 prevent automatic redirect on launch

        classSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (isFirstSelection) {
                    isFirstSelection = false
                    return
                }

                val selected = classNames[position]

                if (selected == "Add Class +") {
                    // Redirect to AddClassActivity only when user selects manually
                    val intent = Intent(this@CalculatorActivity, ClassesActivity::class.java)
                    startActivity(intent)
                } else if (position > 0 && classList.isNotEmpty()) {
                    selectedClass = classList[position - 1]
                    assignments.clear()
                    assignmentsContainer.removeAllViews()
                    tableHeader.visibility = View.GONE
                    resultContainer.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }


    private fun showAddAssignmentDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_add_assignment)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val assignmentNameInput = dialog.findViewById<EditText>(R.id.assignmentNameInput)
        val saveBtn = dialog.findViewById<FrameLayout>(R.id.saveAssignmentBtn)
        val closeBtn = dialog.findViewById<ImageView>(R.id.closeDialogBtn)

        saveBtn.setOnClickListener {
            val name = assignmentNameInput.text.toString().trim()
            if (name.isNotEmpty()) {
                val assignment = Assignment(name)
                assignments.add(assignment)
                addAssignmentView(assignment)
                tableHeader.visibility = View.VISIBLE
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Please enter assignment name", Toast.LENGTH_SHORT).show()
            }
        }

        closeBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun addAssignmentView(assignment: Assignment) {
        val assignmentView = layoutInflater.inflate(R.layout.item_assignment, assignmentsContainer, false)

        val assignmentName = assignmentView.findViewById<TextView>(R.id.assignmentName)
        val gradeInput = assignmentView.findViewById<EditText>(R.id.gradeInput)
        val weightInput = assignmentView.findViewById<EditText>(R.id.weightInput)

        assignmentName.text = assignment.name

        // Store references to inputs
        assignment.gradeInputView = gradeInput
        assignment.weightInputView = weightInput

        assignmentsContainer.addView(assignmentView)
    }

    private fun calculateGrade() {
        if (assignments.isEmpty()) {
            Toast.makeText(this, "Please add assignments first", Toast.LENGTH_SHORT).show()
            return
        }

        var totalWeightedGrade = 0.0
        var totalWeight = 0.0
        var hasError = false

        for (assignment in assignments) {
            val gradeText = assignment.gradeInputView?.text.toString().replace("%", "").trim()
            val weightText = assignment.weightInputView?.text.toString().replace("%", "").trim()

            if (gradeText.isEmpty() || weightText.isEmpty()) {
                Toast.makeText(this, "Please fill in all grades and weights", Toast.LENGTH_SHORT).show()
                hasError = true
                break
            }

            try {
                val grade = gradeText.toDouble()
                val weight = weightText.toDouble()

                if (grade < 0 || grade > 100 || weight < 0 || weight > 100) {
                    Toast.makeText(this, "Grades and weights must be between 0 and 100", Toast.LENGTH_SHORT).show()
                    hasError = true
                    break
                }

                totalWeightedGrade += (grade * weight / 100)
                totalWeight += weight

            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show()
                hasError = true
                break
            }
        }

        if (!hasError) {
            if (totalWeight != 100.0) {
                Toast.makeText(this, "Warning: Total weight is ${totalWeight}%, not 100%", Toast.LENGTH_LONG).show()
            }

            val finalGrade = if (totalWeight > 0) totalWeightedGrade else 0.0
            displayResult(finalGrade)
        }
    }

    private fun displayResult(grade: Double) {
        finalGradeText.text = String.format("%.1f%%", grade)

        val letter = when {
            grade >= 90 -> "A"
            grade >= 80 -> "B"
            grade >= 70 -> "C"
            grade >= 60 -> "D"
            else -> "F"
        }

        gradeLetterText.text = letter
        resultContainer.visibility = View.VISIBLE


    }

    private fun setupNavbar() {
        findViewById<FrameLayout>(R.id.nav_tasks).setOnClickListener {
            val intent = Intent(this, ClassesActivity::class.java)
            startActivity(intent)
        }

        findViewById<FrameLayout>(R.id.nav_calendar).setOnClickListener {
            // Navigate to Calendar
        }

        findViewById<FrameLayout>(R.id.nav_marketplace).setOnClickListener {
            // Navigate to Marketplace
        }
    }

    override fun onResume() {
        super.onResume()
        // Reload classes when returning from AddClassActivity
        loadClasses()
        setupClassSpinner()
    }
}

// Assignment data class
data class Assignment(
    val name: String,
    var gradeInputView: EditText? = null,
    var weightInputView: EditText? = null
)