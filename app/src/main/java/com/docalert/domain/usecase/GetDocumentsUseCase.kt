package com.docalert.domain.usecase

import com.docalert.data.local.entity.DocumentEntity
import com.docalert.data.repository.DocumentRepository
import com.docalert.domain.model.Document
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetDocumentsUseCase @Inject constructor(
    private val repository: DocumentRepository
) {
    operator fun invoke(): Flow<List<Document>> {
        return repository.getAllDocuments().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getByCategory(category: String): Flow<List<Document>> {
        return repository.getDocumentsByCategory(category).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getActive(): Flow<List<Document>> {
        return repository.getActiveDocuments().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getExpired(): Flow<List<Document>> {
        return repository.getExpiredDocuments().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun getById(id: Long): Document? {
        return repository.getDocumentById(id)?.toDomain()
    }

    private fun DocumentEntity.toDomain() = Document(
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
