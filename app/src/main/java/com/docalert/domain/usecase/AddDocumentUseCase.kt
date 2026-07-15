package com.docalert.domain.usecase

import com.docalert.data.local.entity.DocumentEntity
import com.docalert.data.repository.DocumentRepository
import com.docalert.domain.model.Document
import javax.inject.Inject

class AddDocumentUseCase @Inject constructor(
    private val repository: DocumentRepository
) {
    suspend operator fun invoke(document: Document): Long {
        return repository.insertDocument(document.toEntity())
    }

    suspend operator fun invoke(documentEntity: DocumentEntity): Long {
        return repository.insertDocument(documentEntity)
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
