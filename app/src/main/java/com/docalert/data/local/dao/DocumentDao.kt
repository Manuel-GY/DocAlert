package com.docalert.data.local.dao

import androidx.room.*
import com.docalert.data.local.entity.DocumentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {

    @Query("SELECT * FROM documents ORDER BY expiryDate ASC")
    fun getAllDocuments(): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE category = :category ORDER BY expiryDate ASC")
    fun getDocumentsByCategory(category: String): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE isExpired = 0 ORDER BY expiryDate ASC")
    fun getActiveDocuments(): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE isExpired = 1 ORDER BY expiryDate DESC")
    fun getExpiredDocuments(): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE id = :documentId")
    suspend fun getDocumentById(documentId: Long): DocumentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: DocumentEntity): Long

    @Update
    suspend fun updateDocument(document: DocumentEntity)

    @Delete
    suspend fun deleteDocument(document: DocumentEntity)

    @Query("DELETE FROM documents WHERE id = :documentId")
    suspend fun deleteDocumentById(documentId: Long)

    @Query("UPDATE documents SET isExpired = :isExpired WHERE id = :documentId")
    suspend fun updateDocumentExpiredStatus(documentId: Long, isExpired: Boolean)

    @Query("UPDATE documents SET calendarEventId = :eventId WHERE id = :documentId")
    suspend fun updateCalendarEventId(documentId: Long, eventId: Long?)

    @Query("SELECT * FROM documents WHERE expiryDate <= :timestamp AND isExpired = 0")
    suspend fun getDocumentsExpiringBefore(timestamp: Long): List<DocumentEntity>
}
