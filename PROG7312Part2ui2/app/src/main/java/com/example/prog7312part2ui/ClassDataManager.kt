package com.example.prog7312part2ui

import com.example.prog7312part2ui.models.Classroom

object ClassDataManager {
    private val classes = mutableListOf<Classroom>()

    fun addClass(classroom: Classroom) {
        classes.add(classroom)
    }

    fun getClasses(): List<Classroom> {
        return classes.toList()
    }

    fun clearClasses() {
        classes.clear()
    }
}