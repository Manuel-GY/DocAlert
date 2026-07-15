package com.docalert.util

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateUtils {

    private val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val fullFormat = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("es"))
    private val shortFormat = SimpleDateFormat("dd MMM yyyy", Locale("es"))

    fun formatDisplayDate(timestamp: Long): String {
        return displayFormat.format(Date(timestamp))
    }

    fun formatFullDate(timestamp: Long): String {
        return fullFormat.format(Date(timestamp))
    }

    fun formatShortDate(timestamp: Long): String {
        return shortFormat.format(Date(timestamp))
    }

    fun daysUntilExpiry(expiryDate: Long): Int {
        val diff = expiryDate - System.currentTimeMillis()
        return TimeUnit.MILLISECONDS.toDays(diff).toInt()
    }

    fun isExpired(expiryDate: Long): Boolean {
        return expiryDate < System.currentTimeMillis()
    }

    fun getExpiryStatus(expiryDate: Long): ExpiryStatus {
        val days = daysUntilExpiry(expiryDate)
        return when {
            days < 0 -> ExpiryStatus.EXPIRED
            days == 0 -> ExpiryStatus.EXPIRES_TODAY
            days == 1 -> ExpiryStatus.EXPIRES_TOMORROW
            days <= 7 -> ExpiryStatus.EXPIRES_SOON
            days <= 30 -> ExpiryStatus.EXPIRES_THIS_MONTH
            else -> ExpiryStatus.VALID
        }
    }

    fun addDays(timestamp: Long, days: Int): Long {
        return timestamp + TimeUnit.DAYS.toMillis(days.toLong())
    }

    fun subtractDays(timestamp: Long, days: Int): Long {
        return timestamp - TimeUnit.DAYS.toMillis(days.toLong())
    }

    fun parseDate(dateString: String): Long? {
        val formats = listOf(
            "dd/MM/yyyy",
            "dd-MM-yyyy",
            "dd.MM.yyyy",
            "MM/dd/yyyy",
            "yyyy-MM-dd"
        )

        for (format in formats) {
            try {
                val sdf = SimpleDateFormat(format, Locale.getDefault())
                sdf.isLenient = false
                val date = sdf.parse(dateString)
                if (date != null) {
                    return date.time
                }
            } catch (_: Exception) {
            }
        }
        return null
    }
}

enum class ExpiryStatus {
    EXPIRED,
    EXPIRES_TODAY,
    EXPIRES_TOMORROW,
    EXPIRES_SOON,
    EXPIRES_THIS_MONTH,
    VALID
}
