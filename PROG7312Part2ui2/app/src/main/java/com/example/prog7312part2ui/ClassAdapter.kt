package com.example.prog7312part2ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.prog7312part2ui.models.Classroom

class ClassAdapter(private val classList: MutableList<Classroom>) :
    RecyclerView.Adapter<ClassAdapter.ClassViewHolder>() {

    inner class ClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val className: TextView = itemView.findViewById(R.id.className)
        val classDescription: TextView = itemView.findViewById(R.id.classDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_class_card, parent, false)
        return ClassViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        val classItem = classList[position]

        // Set class name
        holder.className.text = classItem.title

        // Format the description section
        val descriptionText = buildString {
            append("Semester: ${classItem.semester}\n")
            append("Teacher: ${classItem.teacher.ifEmpty { "Not specified" }}\n")
            append("Description: ${classItem.description.ifEmpty { "None" }}")
        }

        holder.classDescription.text = descriptionText
    }

    override fun getItemCount(): Int = classList.size
}