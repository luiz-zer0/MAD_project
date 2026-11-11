package com.example.app_1.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import java.util.Calendar

internal val reportCategories = listOf("Bags", "Electronics", "Keys", "Clothing", "Documents", "Wallet", "Other")
private val campusLocations = listOf("Library", "Student Centre", "Main Gate", "Auditorium", "Sports Complex", "Business School")
private val ink = Color(0xFF0F2942)
private val muted = Color(0xFF475569)

@Composable
internal fun CategoryPicker(value: String, onSelect: (String) -> Unit, isError: Boolean = false) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            placeholder = { Text("Choose a category", color = muted) },
            trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Choose category", tint = ink) },
            shape = RoundedCornerShape(12.dp),
            textStyle = TextStyle(color = ink),
            isError = isError,
        )
        Box(Modifier.matchParentSize().clickable { expanded = true })
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            reportCategories.forEach { option ->
                DropdownMenuItem(text = { Text(option, color = ink) }, onClick = {
                    onSelect(option)
                    expanded = false
                })
            }
        }
    }
    if (isError) Text("Select a category", color = Color(0xFFB42318))
}

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

@Composable
internal fun CampusLocationPicker(value: String, onSelect: (String) -> Unit, isError: Boolean = false) {
    var showDialog by remember { mutableStateOf(false) }
    var customLocation by remember { mutableStateOf("") }
    Box {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            placeholder = { Text("Choose a campus location", color = muted) },
            trailingIcon = { Icon(Icons.Default.LocationOn, contentDescription = "Choose location", tint = ink) },
            shape = RoundedCornerShape(12.dp),
            textStyle = TextStyle(color = ink),
            isError = isError,
        )
        Box(Modifier.matchParentSize().clickable { showDialog = true })
    }
    if (isError) Text("Select a location", color = Color(0xFFB42318))
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Where on campus?", color = ink) },
            text = {
                Column {
                    campusLocations.forEach { location ->
                        Text(
                            location,
                            modifier = Modifier.fillMaxWidth().clickable {
                                onSelect(location)
                                showDialog = false
                            }.padding(vertical = 8.dp),
                            color = ink,
                        )
                    }
                    OutlinedTextField(
                        value = customLocation,
                        onValueChange = { customLocation = it },
                        label = { Text("Or enter a specific location") },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = ink),
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (customLocation.isNotBlank()) {
                        onSelect(customLocation.trim())
                        showDialog = false
                    }
                }) { Text("Use location") }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancel") } },
        )
    }
}
