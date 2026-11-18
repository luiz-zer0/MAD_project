package com.example.app_1.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import java.util.Calendar

private val ink = Color(0xFF0F2942)
private val muted = Color(0xFF475569)

@Composable
internal fun CatalogPicker(label: String, entries: List<CatalogEntry>, value: String,
    onSelect: (String) -> Unit, isError: Boolean = false) {
    var open by remember { mutableStateOf(false) }
    var search by remember { mutableStateOf("") }
    val selected = ChalaCatalog.resolve(entries, value)
    OutlinedButton(onClick = { search = ""; open = true }, modifier = Modifier.fillMaxWidth()) {
        Text(selected?.displayLabel ?: value.ifBlank { "Choose $label" })
    }
    if (value.isNotBlank() && selected == null) Text("Unclassified legacy input: $value. Select a catalog option; your original draft is retained until you save.")
    if (isError) Text("Select $label", color = MaterialTheme.colorScheme.error)
    if (open) AlertDialog(onDismissRequest = { open = false }, title = { Text(label) },
        text = {
            Column {
                OutlinedTextField(search, { search = it }, label = { Text("Search $label") }, modifier = Modifier.fillMaxWidth())
                LazyColumn(Modifier.fillMaxWidth().heightIn(max = 280.dp)) {
                    val filtered = entries.filter { it.displayLabel.contains(search, true) || it.sourceLabel.contains(search, true) }
                    if (filtered.isEmpty()) item { Text("No matching options", Modifier.padding(16.dp)) }
                    items(filtered, key = { it.id }) { entry ->
                        TextButton(onClick = { onSelect(entry.id); open = false }, modifier = Modifier.fillMaxWidth()) {
                            Text(entry.displayLabel, modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            }
        }, confirmButton = { TextButton(onClick = { open = false }) { Text("Close") } })
}
@Composable
internal fun CategoryPicker(value: String, onSelect: (String) -> Unit, isError: Boolean = false) =
    CatalogPicker("Category", ChalaCatalog.categories, value, onSelect, isError)
@Composable
internal fun CampusLocationPicker(value: String, onSelect: (String) -> Unit, isError: Boolean = false) =
    CatalogPicker("Location", ChalaCatalog.locations, value, onSelect, isError)

@Composable
internal fun ReportDatePicker(value: String, onSelect: (String) -> Unit, isError: Boolean = false) {
    val context = LocalContext.current
    val today = Calendar.getInstance()
    val openPicker = {
        val initial = Calendar.getInstance()
        val parts = value.split("-").map { it.toIntOrNull() }
        if (parts.size == 3 && parts.all { it != null }) {
            initial.set(parts[0]!!, parts[1]!! - 1, parts[2]!!)
        }
        DatePickerDialog(context, { _, year, month, day ->
            onSelect("%04d-%02d-%02d".format(year, month + 1, day))
        }, initial.get(Calendar.YEAR), initial.get(Calendar.MONTH), initial.get(Calendar.DAY_OF_MONTH)).apply {
            datePicker.maxDate = System.currentTimeMillis()
        }.show()
    }
    Box {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            placeholder = { Text("Select date", color = muted) },
            trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = "Choose date", tint = ink) },
            shape = RoundedCornerShape(12.dp),
            textStyle = TextStyle(color = ink),
            isError = isError,
        )
        Box(Modifier.matchParentSize().clickable { openPicker() })
    }
    if (isError) Text("Select a date", color = Color(0xFFB42318))
}
