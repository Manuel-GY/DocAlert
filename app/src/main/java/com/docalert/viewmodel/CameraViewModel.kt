package com.docalert.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.docalert.data.repository.OCRRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    application: Application,
    private val ocrRepository: OCRRepository
) : AndroidViewModel(application) {

    private val _extractedDate = MutableStateFlow<Long?>(null)
    val extractedDate: StateFlow<Long?> = _extractedDate.asStateFlow()

    private val _extractedText = MutableStateFlow<String>("")
    val extractedText: StateFlow<String> = _extractedText.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    fun processImage(uri: Uri) {
        viewModelScope.launch {
            _isProcessing.value = true
            ocrRepository.processImage(
                imageUri = uri,
                onSuccess = { date, text ->
                    _extractedDate.value = date
                    _extractedText.value = text
                    _isProcessing.value = false
                },
                onFailure = { _isProcessing.value = false }
            )
        }
    }

    fun clearExtractedData() {
        _extractedDate.value = null
        _extractedText.value = ""
    }
}
