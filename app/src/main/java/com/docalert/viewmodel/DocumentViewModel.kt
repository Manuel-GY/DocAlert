package com.docalert.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.docalert.data.local.entity.DocumentEntity
import com.docalert.data.repository.CalendarRepository
import com.docalert.data.repository.DocumentRepository
import com.docalert.domain.model.Document
import com.docalert.domain.usecase.AddDocumentUseCase
import com.docalert.domain.usecase.DeleteDocumentUseCase
import com.docalert.domain.usecase.GetDocumentsUseCase
import com.docalert.util.DateUtils
import com.docalert.util.NotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DocumentViewModel @Inject constructor(
    application: Application,
    private val getDocumentsUseCase: GetDocumentsUseCase,
    private val addDocumentUseCase: AddDocumentUseCase,
    private val deleteDocumentUseCase: DeleteDocumentUseCase,
    private val documentRepository: DocumentRepository,
    private val calendarRepository: CalendarRepository
) : AndroidViewModel(application) {

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _currentDocument = MutableStateFlow<Document?>(null)
    val currentDocument: StateFlow<Document?> = _currentDocument.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    val documents: StateFlow<List<Document>> = _selectedCategory
        .flatMapLatest { category ->
            if (category == null) {
                getDocumentsUseCase()
            } else {
                getDocumentsUseCase().map { docs ->
                    docs.filter { it.category == category }
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        NotificationHelper.createNotificationChannel(application)
        checkExpiredDocuments()
    }

    fun selectCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun loadDocument(id: Long) {
        viewModelScope.launch {
            _currentDocument.value = getDocumentsUseCase.getById(id)
        }
    }

    fun saveDocument(
        name: String,
        category: String,
        expiryDate: Long,
        notes: String?,
        reminderDays: Int
    ) {
        viewModelScope.launch {
            val document = Document(
                name = name,
                category = category,
                expiryDate = expiryDate,
                notes = notes,
                reminderDays = reminderDays,
                isExpired = DateUtils.isExpired(expiryDate)
            )

            val id = addDocumentUseCase(document)
            _currentDocument.value = document.copy(id = id)

            if (DateUtils.isExpired(expiryDate)) {
                documentRepository.updateDocumentExpiredStatus(id, true)
            }

            _uiEvent.emit(UiEvent.DocumentSaved)
        }
    }

    fun updateDocument(
        documentId: Long,
        name: String,
        category: String,
        expiryDate: Long,
        notes: String?,
        reminderDays: Int
    ) {
        viewModelScope.launch {
            val existingDoc = documentRepository.getDocumentById(documentId)
            if (existingDoc != null) {
                val updatedDoc = existingDoc.copy(
                    name = name,
                    category = category,
                    expiryDate = expiryDate,
                    notes = notes,
                    reminderDays = reminderDays,
                    isExpired = DateUtils.isExpired(expiryDate)
                )
                documentRepository.updateDocument(updatedDoc)
                _currentDocument.value = updatedDoc.copy(id = documentId)
                _uiEvent.emit(UiEvent.DocumentSaved)
            }
        }
    }

    fun deleteDocument(documentId: Long) {
        viewModelScope.launch {
            val doc = documentRepository.getDocumentById(documentId)
            if (doc?.calendarEventId != null) {
                calendarRepository.deleteEvent(doc.calendarEventId)
            }
            deleteDocumentUseCase(documentId)
            _uiEvent.emit(UiEvent.DocumentDeleted)
        }
    }

    fun addToCalendar(documentId: Long) {
        viewModelScope.launch {
            val doc = documentRepository.getDocumentById(documentId)
            if (doc != null) {
                val eventId = calendarRepository.addEventToCalendar(
                    title = "Vence: ${doc.name}",
                    description = "El documento '${doc.name}' (${doc.category}) vence el ${DateUtils.formatDisplayDate(doc.expiryDate)}",
                    startMillis = doc.expiryDate,
                    reminderDays = doc.reminderDays
                )

                if (eventId != null) {
                    documentRepository.updateCalendarEventId(documentId, eventId)
                    _currentDocument.value = doc.copy(calendarEventId = eventId)
                    _uiEvent.emit(UiEvent.CalendarEventAdded)
                } else {
                    _uiEvent.emit(UiEvent.Error("No se pudo agregar al calendario"))
                }
            }
        }
    }

    fun markAsExpired(documentId: Long) {
        viewModelScope.launch {
            documentRepository.updateDocumentExpiredStatus(documentId, true)
            _currentDocument.value = _currentDocument.value?.copy(isExpired = true)
        }
    }

    fun markAsActive(documentId: Long) {
        viewModelScope.launch {
            documentRepository.updateDocumentExpiredStatus(documentId, false)
            _currentDocument.value = _currentDocument.value?.copy(isExpired = false)
        }
    }

    private fun checkExpiredDocuments() {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val expiringDocs = documentRepository.getDocumentsExpiringBefore(now)
            expiringDocs.forEach { doc ->
                if (!doc.isExpired) {
                    documentRepository.updateDocumentExpiredStatus(doc.id, true)
                }
            }
        }
    }

    sealed class UiEvent {
        data object DocumentSaved : UiEvent()
        data object DocumentDeleted : UiEvent()
        data object CalendarEventAdded : UiEvent()
        data class Error(val message: String) : UiEvent()
    }
}
