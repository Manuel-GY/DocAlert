package com.docalert.domain.model

data class Document(
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
) {
    val daysUntilExpiry: Int
        get() {
            val diff = expiryDate - System.currentTimeMillis()
            return (diff / (1000 * 60 * 60 * 24)).toInt()
        }

    val statusText: String
        get() = when {
            isExpired -> "VENCIDO"
            daysUntilExpiry <= 0 -> "Vence hoy"
            daysUntilExpiry == 1 -> "Vence mañana"
            daysUntilExpiry <= 7 -> "Vence en $daysUntilExpiry días"
            daysUntilExpiry <= 30 -> "Vence en $daysUntilExpiry días"
            else -> "Vence en $daysUntilExpiry días"
        }

    companion object {
        val CATEGORIES = listOf(
            "Cédula de identidad",
            "Carnet de conducir",
            "Permiso de circulación",
            "Pasaporte",
            "Visa",
            "Seguro vehicular",
            "Certificado médico",
            "Libreta de salud",
            "Documento de residencia",
            "Otro"
        )
    }
}
