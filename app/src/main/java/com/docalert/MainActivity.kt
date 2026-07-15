package com.docalert

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.docalert.ui.screens.adddocument.AddDocumentScreen
import com.docalert.ui.screens.camera.CameraScreen
import com.docalert.ui.screens.detail.DocumentDetailScreen
import com.docalert.ui.screens.home.HomeScreen
import com.docalert.ui.theme.DocAlertTheme
import com.docalert.util.DateUtils
import com.docalert.viewmodel.DocumentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DocAlertTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DocAlertNavHost()
                }
            }
        }
    }
}

@Composable
fun DocAlertNavHost(
    viewModel: DocumentViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val documents by viewModel.documents.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val currentDocument by viewModel.currentDocument.collectAsState()

    var pendingExtractedDate by remember { mutableStateOf<Long?>(null) }
    var pendingExtractedText by remember { mutableStateOf<String>("") }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is DocumentViewModel.UiEvent.DocumentSaved -> {
                    Toast.makeText(context, "Documento guardado", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
                is DocumentViewModel.UiEvent.DocumentDeleted -> {
                    Toast.makeText(context, "Documento eliminado", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
                is DocumentViewModel.UiEvent.CalendarEventAdded -> {
                    Toast.makeText(context, "Agregado al calendario", Toast.LENGTH_SHORT).show()
                }
                is DocumentViewModel.UiEvent.Error -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                documents = documents,
                selectedCategory = selectedCategory,
                onCategorySelected = { viewModel.selectCategory(it) },
                onDocumentClick = { document ->
                    viewModel.loadDocument(document.id)
                    navController.navigate("detail/${document.id}")
                },
                onAddDocumentClick = {
                    pendingExtractedDate = null
                    pendingExtractedText = ""
                    navController.navigate("add")
                }
            )
        }

        composable("add") {
            AddDocumentScreen(
                document = null,
                isEditing = false,
                onSave = { name, category, expiryDate, notes, reminderDays ->
                    viewModel.saveDocument(name, category, expiryDate, notes, reminderDays)
                },
                onTakePhoto = {
                    navController.navigate("camera")
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "edit/{documentId}",
            arguments = listOf(navArgument("documentId") { type = NavType.LongType })
        ) { backStackEntry ->
            val documentId = backStackEntry.arguments?.getLong("documentId") ?: return@composable
            val doc = currentDocument

            if (doc != null && doc.id == documentId) {
                AddDocumentScreen(
                    document = doc,
                    isEditing = true,
                    onSave = { name, category, expiryDate, notes, reminderDays ->
                        viewModel.updateDocument(documentId, name, category, expiryDate, notes, reminderDays)
                    },
                    onTakePhoto = {
                        navController.navigate("camera")
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }

        composable(
            route = "detail/{documentId}",
            arguments = listOf(navArgument("documentId") { type = NavType.LongType })
        ) { backStackEntry ->
            val documentId = backStackEntry.arguments?.getLong("documentId") ?: return@composable
            val doc = currentDocument

            DocumentDetailScreen(
                document = doc,
                onEdit = {
                    navController.navigate("edit/$documentId")
                },
                onDelete = {
                    viewModel.deleteDocument(documentId)
                    navController.popBackStack()
                },
                onAddToCalendar = {
                    viewModel.addToCalendar(documentId)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("camera") {
            CameraScreen(
                onDateExtracted = { date, text ->
                    pendingExtractedDate = date
                    pendingExtractedText = text
                    navController.popBackStack()
                    navController.navigate("add_with_ocr/${date ?: 0}")
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "add_with_ocr/{extractedDate}",
            arguments = listOf(navArgument("extractedDate") { type = NavType.LongType })
        ) { backStackEntry ->
            val extractedDate = backStackEntry.arguments?.getLong("extractedDate") ?: 0L
            val initialDate = if (extractedDate > 0) extractedDate else System.currentTimeMillis()

            AddDocumentScreen(
                document = null,
                isEditing = false,
                onSave = { name, category, expiryDate, notes, reminderDays ->
                    viewModel.saveDocument(name, category, expiryDate, notes, reminderDays)
                },
                onTakePhoto = {
                    navController.navigate("camera")
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
