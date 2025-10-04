package com.example.prog7312part2ui.models

import java.io.Serializable
data class Classroom(
    val title: String,
    val teacher: String,
    val location: String,
    val description: String,
    val isAllYear: Boolean,
    val isRecurring: Boolean,
    val semester: String
) : java.io.Serializable