package com.docalert.data.repository

import com.docalert.data.local.dao.DocumentDao
import com.docalert.data.local.entity.DocumentEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocumentRepository @Inject constructor(
    private val documentDao: DocumentDao
) {
    fun getAllDocuments(): Flow<List<DocumentEntity>> {
        return documentDao.getAllDocuments()
    }

    fun getDocumentsByCategory(category: String): Flow<List<DocumentEntity>> {
        return documentDao.getDocumentsByCategory(category)
    }

    fun getActiveDocuments(): Flow<List<DocumentEntity>> {
        return documentDao.getActiveDocuments()
    }

    fun getExpiredDocuments(): Flow<List<DocumentEntity>> {
        return documentDao.getExpiredDocuments()
    }

    suspend fun getDocumentById(id: Long): DocumentEntity? {
        return documentDao.getDocumentById(id)
    }

    suspend fun insertDocument(document: DocumentEntity): Long {
        return documentDao.insertDocument(document)
    }

    suspend fun updateDocument(document: DocumentEntity) {
        documentDao.updateDocument(document)
    }

    suspend fun deleteDocument(document: DocumentEntity) {
        documentDao.deleteDocument(document)
    }

    suspend fun deleteDocumentById(id: Long) {
        documentDao.deleteDocumentById(id)
    }

    suspend fun updateDocumentExpiredStatus(id: Long, isExpired: Boolean) {
        documentDao.updateDocumentExpiredStatus(id, isExpired)
    }

    suspend fun updateCalendarEventId(id: Long, eventId: Long?) {
        documentDao.updateCalendarEventId(id, eventId)
    }

    suspend fun getDocumentsExpiringBefore(timestamp: Long): List<DocumentEntity> {
        return documentDao.getDocumentsExpiringBefore(timestamp)
    }
}
