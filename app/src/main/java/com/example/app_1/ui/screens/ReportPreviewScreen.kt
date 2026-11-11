package com.example.app_1.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** General sample report details. No private ownership evidence is shown. */
@Composable
fun ReportPreviewScreen(onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
        Text("Sample found report", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F2942))
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Set of keys", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("Category: Keys")
                Text("Location: Student Centre · cafeteria")
                Text("Date reported: 2026-10-15")
                Text("Status: Sample office receipt")
                Text("A set of keys on a metal ring. Private identifiers are hidden.")
            }
        }
        Text("This is sample data. No report is stored on a server.", color = Color(0xFF475569))
        Button(onClick = onBack) { Text("Back to reports") }
    }
}
