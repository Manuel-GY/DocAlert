package com.docalert.data.repository

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OCRRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val recognizer: TextRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private val datePatterns = listOf(
        "dd/MM/yyyy",
        "dd-MM-yyyy",
        "dd.MM.yyyy",
        "MM/dd/yyyy",
        "MM-dd-yyyy",
        "yyyy/MM/dd",
        "yyyy-MM-dd",
        "yyyy.MM.dd",
        "dd/MM/yy",
        "dd-MM-yy",
        "d/M/yyyy",
        "d-M-yyyy",
        "d/MM/yyyy",
        "dd/MMMM/yyyy",
        "dd MMMM yyyy",
        "dd MMM yyyy",
        "MMMM dd, yyyy",
        "MMM dd, yyyy"
    )

    private val dateRegexPatterns = listOf(
        Regex("""\d{1,2}[/\-.]\d{1,2}[/\-.]\d{2,4}"""),
        Regex("""\d{4}[/\-.]\d{1,2}[/\-.]\d{1,2}"""),
        Regex("""\d{1,2}\s+(?:de\s+)?(?:ene|feb|mar|abr|may|jun|jul|ago|sep|oct|nov|dic)[a-z]*\.?\s+(?:de\s+)?\d{2,4}""", RegexOption.IGNORE_CASE),
        Regex("""(?:ene|feb|mar|abr|may|jun|jul|ago|sep|oct|nov|dic)[a-z]*\.?\s+\d{1,2},?\s+\d{2,4}""", RegexOption.IGNORE_CASE),
        Regex("""\d{1,2}\s+(?:January|February|March|April|May|June|July|August|September|October|November|December)\s+\d{2,4}""", RegexOption.IGNORE_CASE),
        Regex("""(?:January|February|March|April|May|June|July|August|September|October|November|December)\s+\d{1,2},?\s+\d{2,4}""", RegexOption.IGNORE_CASE)
    )

    private val expiryKeywords = listOf(
        "vencimiento", "vence", "expir", "validez", "válido",
        "hasta", "until", "expiry", "expiration", "valid until",
        "fecha de vencimiento", "fecha límite", "date of expiry",
        "exp date", "valid thru", "valid through"
    )

    fun processImage(
        imageUri: Uri,
        onSuccess: (extractedDate: Long?, allText: String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            val image = InputImage.fromFilePath(context, imageUri)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val allText = visionText.text
                    val extractedDate = extractDateFromText(allText)
                    onSuccess(extractedDate, allText)
                }
                .addOnFailureListener { e ->
                    onFailure(e)
                }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    private fun extractDateFromText(text: String): Long? {
        val lines = text.lines()

        for (keyword in expiryKeywords) {
            for (line in lines) {
                if (line.lowercase().contains(keyword.lowercase())) {
                    val dateFromLine = extractDateFromString(line)
                    if (dateFromLine != null) return dateFromLine
                }
            }
        }

        for (pattern in dateRegexPatterns) {
            val matches = pattern.findAll(text)
            for (match in matches) {
                val date = parseDateString(match.value)
                if (date != null && date > System.currentTimeMillis()) {
                    return date
                }
            }
        }

        for (pattern in dateRegexPatterns) {
            val matches = pattern.findAll(text)
            for (match in matches) {
                val date = parseDateString(match.value)
                if (date != null) {
                    return date
                }
            }
        }

        return null
    }

    private fun extractDateFromString(text: String): Long? {
        for (pattern in dateRegexPatterns) {
            val match = pattern.find(text)
            if (match != null) {
                return parseDateString(match.value)
            }
        }
        return null
    }

    private fun parseDateString(dateStr: String): Long? {
        val cleanDate = dateStr.trim()

        for (pattern in datePatterns) {
            try {
                val sdf = SimpleDateFormat(pattern, Locale.getDefault())
                sdf.isLenient = false
                val date = sdf.parse(cleanDate)
                if (date != null) {
                    return date.time
                }
            } catch (_: Exception) {
            }
        }

        val normalized = cleanDate
            .replace("ene", "Jan").replace("feb", "Feb").replace("mar", "Mar")
            .replace("abr", "Apr").replace("may", "May").replace("jun", "Jun")
            .replace("jul", "Jul").replace("ago", "Aug").replace("sep", "Sep")
            .replace("oct", "Oct").replace("nov", "Nov").replace("dic", "Dec")
            .replace("de ", "")

        for (pattern in datePatterns) {
            try {
                val sdf = SimpleDateFormat(pattern, Locale.ENGLISH)
                sdf.isLenient = false
                val date = sdf.parse(normalized)
                if (date != null) {
                    return date.time
                }
            } catch (_: Exception) {
            }
        }

        return null
    }
}
