package com.example.prog7312part2ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prog7312part2ui.models.Classroom

class ClassesActivity : AppCompatActivity() {

    private lateinit var classesRecyclerView: RecyclerView
    private lateinit var emptyStateContainer: LinearLayout
    private lateinit var addClassButton: FrameLayout
    private lateinit var fabAddClass: FrameLayout
    private lateinit var yearTitle: TextView
    private lateinit var prevYearBtn: ImageView
    private lateinit var nextYearBtn: ImageView

    private val classList = mutableListOf<Classroom>()
    private lateinit var adapter: ClassAdapter
    private var currentYear = 2025

    companion object {
        const val ADD_CLASS_REQUEST = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classes)

        // Initialize views
        classesRecyclerView = findViewById(R.id.classesRecyclerView)
        emptyStateContainer = findViewById(R.id.emptyStateContainer)
        addClassButton = findViewById(R.id.addClassButton)
        fabAddClass = findViewById(R.id.fabAddClass)
        yearTitle = findViewById(R.id.yearTitle)
        prevYearBtn = findViewById(R.id.prevYearBtn)
        nextYearBtn = findViewById(R.id.nextYearBtn)

        // Setup RecyclerView with GridLayoutManager for 2 columns
        adapter = ClassAdapter(classList)
        classesRecyclerView.layoutManager = GridLayoutManager(this, 2)
        classesRecyclerView.adapter = adapter

        // Add class click listener
        val addClassClickListener = View.OnClickListener {
            val intent = Intent(this, AddClassActivity::class.java)
            startActivityForResult(intent, ADD_CLASS_REQUEST)
        }

        addClassButton.setOnClickListener(addClassClickListener)
        fabAddClass.setOnClickListener(addClassClickListener)

        // Year navigation
        prevYearBtn.setOnClickListener {
            currentYear--
            yearTitle.text = " Classroom $currentYear "
            // Here you would load classes for the new year from database
        }

        nextYearBtn.setOnClickListener {
            currentYear++
            yearTitle.text = " Classroom $currentYear "
            // Here you would load classes for the new year from database
        }

        updateUI()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_CLASS_REQUEST && resultCode == RESULT_OK) {
            val newClass = data?.getSerializableExtra("NEW_CLASS") as? Classroom
            newClass?.let {
                classList.add(it)
                ClassDataManager.addClass(it) // Add this line
                adapter.notifyItemInserted(classList.size - 1)
                updateUI()
            }
        }
    }

    private fun updateUI() {
        if (classList.isEmpty()) {
            // Show empty state
            emptyStateContainer.visibility = View.VISIBLE
            classesRecyclerView.visibility = View.GONE
            fabAddClass.visibility = View.GONE
        } else {
            // Show classes grid
            emptyStateContainer.visibility = View.GONE
            classesRecyclerView.visibility = View.VISIBLE
            fabAddClass.visibility = View.VISIBLE
        }
    }
}