package com.example.app_1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun MyReportsScreen(onNavigateToNewReport: () -> Unit, onReviewMatches: () -> Unit,
    modifier: Modifier = Modifier, onSelectReportItem: () -> Unit = {}, onContinueDraft: (Boolean) -> Unit = {}) {
    val drafts = ReportDraftStore.loadAll(LocalContext.current)
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("My Reports", style = MaterialTheme.typography.headlineSmall)
        Button(onClick = onNavigateToNewReport) { Text("General Items · New report") }
        Text("Submitted reports", style = MaterialTheme.typography.titleMedium)
        Text("No submitted reports. Backend submission and status tracking are unavailable.")
        Text("Unfinished reports", style = MaterialTheme.typography.titleMedium)
        Text("One lost and one found draft can be saved locally. Saving replaces that type's previous draft.")
        if (drafts.isEmpty()) Text("No unfinished reports.")
        drafts.forEach { draft ->
            Card(onClick = { onContinueDraft(draft.isFound) }, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(draft.itemName.ifBlank { "Unnamed legacy report" })
                    Text("${if (draft.isFound) "Found" else "Lost"} · Draft · Not submitted")
                    Text(ChalaCatalog.resolve(ChalaCatalog.categories, draft.category)?.displayLabel ?: "Unclassified category: ${draft.category}")
                    Text(ChalaCatalog.resolve(ChalaCatalog.locations, draft.location)?.displayLabel ?: "Unclassified location: ${draft.location}")
                    TextButton(onClick = { onContinueDraft(draft.isFound) }) { Text("Resume unfinished report") }
                }
            }
        }
    }
}
