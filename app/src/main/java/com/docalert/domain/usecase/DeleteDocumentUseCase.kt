package com.docalert.domain.usecase

import com.docalert.data.local.entity.DocumentEntity
import com.docalert.data.repository.DocumentRepository
import com.docalert.domain.model.Document
import javax.inject.Inject

class DeleteDocumentUseCase @Inject constructor(
    private val repository: DocumentRepository
) {
    suspend operator fun invoke(document: Document) {
        repository.deleteDocument(document.toEntity())
    }

    suspend operator fun invoke(documentId: Long) {
        repository.deleteDocumentById(documentId)
    }

    private fun Document.toEntity() = DocumentEntity(
        id = id,
        name = name,
        category = category,
        expiryDate = expiryDate,
        photoUri = photoUri,
        notes = notes,
        calendarEventId = calendarEventId,
        reminderDays = reminderDays,
        createdAt = createdAt,
        isExpired = isExpired
    )
}
