package com.example.app_1.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
internal fun GeneralReportScreen(isFound: Boolean, onBack: () -> Unit, modifier: Modifier,
    category: String, onCategoryChange: (String) -> Unit, itemName: String, onItemNameChange: (String) -> Unit,
    onLostId: () -> Unit, onMyReports: () -> Unit) {
    val context = LocalContext.current
    val saved = remember { ReportDraftStore.load(context, isFound) }
    var description by rememberSaveable { mutableStateOf(saved?.description.orEmpty()) }
    var date by rememberSaveable { mutableStateOf(saved?.date.orEmpty()) }
    var location by rememberSaveable { mutableStateOf(saved?.location.orEmpty()) }
    var photo by rememberSaveable { mutableStateOf(saved?.photoReference.orEmpty()) }
    var errors by rememberSaveable { mutableStateOf(false) }
    var message by rememberSaveable { mutableStateOf("") }
    var details by remember { mutableStateOf(false) }
    // Private proof stays in memory only, never in preferences or saved instance state.
    var privateClues by remember { mutableStateOf("") }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            runCatching { context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) }
                .onSuccess { photo = uri.toString(); message = "Photo selected locally; not uploaded." }
                .onFailure { message = "Could not retain photo access. Please choose another photo." }
        }
    }
    Column(modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        TextButton(onClick = onBack) { Text("Back") }
        Text(if (isFound) "General Items · Found" else "General Items · Lost", style = MaterialTheme.typography.headlineSmall)
        TextButton(onClick = onLostId) { Text("Lost IDs") }
        TextButton(onClick = onMyReports) { Text("My Reports") }
        Text("Local draft mode. Submission, matching and notifications are not connected.")
        OutlinedTextField(itemName, { onItemNameChange(it); message = "" }, label = { Text("Item Name *") },
            modifier = Modifier.fillMaxWidth(), isError = errors && itemName.isBlank())
        Text(if (isFound) "Date found *" else "Date lost *")
        ReportDatePicker(date, { date = it; message = "" }, errors && !validReportDate(date))
        Text("Category *")
        CategoryPicker(category, { onCategoryChange(it); message = "" }, errors && ChalaCatalog.resolve(ChalaCatalog.categories, category) == null)
        Text(if (isFound) "Found location * (not office custody)" else "Last seen location *")
        CampusLocationPicker(location, { location = it; message = "" }, errors && ChalaCatalog.resolve(ChalaCatalog.locations, location) == null)
        OutlinedTextField(description, { description = it; message = "" }, label = { Text("Description *") },
            modifier = Modifier.fillMaxWidth(), minLines = 3, isError = errors && description.isBlank())
        Text(descriptionExample(category), style = MaterialTheme.typography.bodySmall)
        OutlinedButton(onClick = { picker.launch(arrayOf("image/*")) }) { Text(if (photo.isEmpty()) "Add optional photo" else "Replace local photo") }
        if (photo.isNotEmpty()) TextButton(onClick = { photo = "" }) { Text("Remove selected photo") }
        TextButton(onClick = { details = !details }) { Text("Add identifying details (optional)") }
        if (details) {
            Text("Private clues are separate from the description. They are temporary and are not saved or sent. Staff will collect proof at claim review.")
            OutlinedTextField(privateClues, { privateClues = it }, label = { Text("Private identifying clue") }, modifier = Modifier.fillMaxWidth())
        }
        Button(onClick = {
            errors = true
            val draft = BasicReportDraft(isFound, category, description, date, location, itemName, photo)
            message = if (draft.toGeneralRequest() == null) "Complete the five required fields and select canonical category and location."
                else "Report fields are ready. Submission is unavailable: no durable backend is connected. Save your unfinished report below."
        }, modifier = Modifier.fillMaxWidth()) { Text("Check report · submission unavailable") }
        TextButton(onClick = {
            message = if (ReportDraftStore.save(context, BasicReportDraft(isFound, category, description, date, location, itemName, photo)))
                "Unfinished report saved on this device. Not submitted. Private clues were not saved."
            else "Saving failed. Your input remains here; try again."
        }) { Text("Save unfinished report") }
        if (message.isNotEmpty()) Text(message)
    }
}
