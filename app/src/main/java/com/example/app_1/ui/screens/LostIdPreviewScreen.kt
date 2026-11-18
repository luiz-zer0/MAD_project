package com.example.app_1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Synthetic-only demonstration. Numbers and details never enter saved state or preferences. */
@Composable
fun LostIdPreviewScreen(onBack: () -> Unit, onMyReports: () -> Unit) {
    var type by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        TextButton(onClick = onBack) { Text("General Items") }
        TextButton(onClick = onMyReports) { Text("My Reports") }
        Text("Lost IDs · Synthetic preview", style = MaterialTheme.typography.headlineSmall)
        Text("Restricted storage and staff access are not connected. Do not enter real ID numbers or personal details. Nothing here is saved, submitted or notified.")
        CatalogPicker("ID Type", ChalaCatalog.idTypes, type, { type = it; result = "" })
        Text("ID Number: choose a synthetic example. Leading zeros are retained.")
        // Deliberately no free-text real-number entry before restricted persistence is available.
        listOf("00001234", "00005678").forEach { sample ->
            OutlinedButton(onClick = { number = sample; result = "" }) { Text("Use synthetic $sample") }
        }
        Text(if (number.isEmpty()) "No example selected" else "Synthetic number selected · ••••${number.takeLast(2)}")
        Text("Date Lost *")
        ReportDatePicker(date, { date = it; result = "" })
        OutlinedTextField(details, { details = it }, label = { Text("Additional Details (optional, synthetic only)") }, modifier = Modifier.fillMaxWidth())
        Button(onClick = {
            result = if (type.isEmpty() || number.isEmpty() || !validReportDate(date)) "Select an ID type, synthetic number and date."
            else if (type == "student_id" && number.trim() == "00001234") "Synthetic exact match. Staff must verify ownership before release. No notification was sent."
            else "No synthetic exact match. Different types or numbers do not match."
        }) { Text("Run synthetic exact-match preview") }
        Text(result)
    }
}
