package com.example.prog7312part2ui.models

import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
data class Task(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val className: String = "",
    val location: String = "",
    val description: String = "",
    val isAllDay: Boolean = false,
    val startTime: Date? = null,
    val endTime: Date? = null,
    val isRecurring: Boolean = false,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

