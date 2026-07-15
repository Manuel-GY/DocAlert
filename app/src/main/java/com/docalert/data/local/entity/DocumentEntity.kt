package com.docalert.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String,
    val expiryDate: Long,
    val photoUri: String? = null,
    val notes: String? = null,
    val calendarEventId: Long? = null,
    val reminderDays: Int = 30,
    val createdAt: Long = System.currentTimeMillis(),
    val isExpired: Boolean = false
)
