package com.example.app_1.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_1.ui.theme.App_1Theme
import com.example.app_1.ui.theme.LostFoundBlue
import com.example.app_1.ui.theme.LostFoundBorder
import com.example.app_1.ui.theme.LostFoundMuted
import com.example.app_1.ui.theme.LostFoundNavy
import com.example.app_1.ui.theme.LostFoundPageBackground

@Composable
fun FoundItemCheckInScreen(
    onBackToOffice: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // This is a local workflow preview. Nothing is persisted or sent to an office service.
    var step by remember { mutableStateOf(1) }
    var category by remember { mutableStateOf("Bags") }
    var description by remember { mutableStateOf("") }
    var foundDate by remember { mutableStateOf("") }
    var foundAt by remember { mutableStateOf("") }
    var custodyLocation by remember { mutableStateOf("") }
    var receivedBy by remember { mutableStateOf("") }
    var privateEvidence by remember { mutableStateOf("") }
    var condition by remember { mutableStateOf("") }
    var showErrors by remember { mutableStateOf(false) }

    BackHandler(step == 2) { step = 1 }

    Column(modifier.fillMaxSize().background(LostFoundPageBackground)) {
        Row(
            Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 8.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { if (step == 2) step = 1 else onBackToOffice() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = LostFoundNavy)
            }
            Column(Modifier.weight(1f)) {
                Text("Found item check-in", color = LostFoundNavy, fontSize = 16.sp,
                    fontWeight = FontWeight.Bold)
                Text("STRATHMORE · LOST & FOUND OFFICE", color = LostFoundMuted, fontSize = 8.sp)
            }
            Text("PREVIEW", color = LostFoundBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 12.dp))
        }
        HorizontalDivider(color = LostFoundBorder)
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .padding(horizontal = 17.dp, vertical = 15.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp),
        ) {
            Text("STEP $step OF 2 · ${if (step == 1) "RECEIPT & CUSTODY" else "PRIVATE EVIDENCE"}",
                color = LostFoundBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(Modifier.weight(1f).height(3.dp).background(LostFoundBlue, RoundedCornerShape(2.dp)))
                Box(Modifier.weight(1f).height(3.dp).background(
                    if (step == 2) LostFoundBlue else LostFoundBorder, RoundedCornerShape(2.dp)))
            }
            if (step == 1) {
                Text("Receive the item in person. On check-in, active lost reports can be checked for new matches.",
                    color = LostFoundMuted, fontSize = 11.sp, lineHeight = 16.sp)
                CategorySelector(category, onSelect = { category = it })
                CheckInField("General description", description, { description = it },
                    "e.g. navy backpack with a front zip pocket", showErrors && description.isBlank())
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.weight(1f)) {
                        CheckInField("Date found", foundDate, { foundDate = it },
                            "DD MMM YYYY", showErrors && foundDate.isBlank())
                    }
                    Box(Modifier.weight(1f)) {
                        CheckInField("Found at", foundAt, { foundAt = it },
                            "e.g. Library", showErrors && foundAt.isBlank())
                    }
                }
                CheckInField("Custody location", custodyLocation, { custodyLocation = it },
                    "e.g. Student Centre · Shelf A / Bin 3", showErrors && custodyLocation.isBlank())
                CheckInField("Received by / physical status", receivedBy, { receivedBy = it },
                    "Staff name and receipt condition", showErrors && receivedBy.isBlank())
                SafetyNotice("Keep hidden marks, contents and personal details out of general descriptions and photos.")
                Spacer(Modifier.height(4.dp))
                Button(
                    onClick = {
                        showErrors = true
                        if (description.isNotBlank() && foundDate.isNotBlank() && foundAt.isNotBlank() &&
                            custodyLocation.isNotBlank() && receivedBy.isNotBlank()) step = 2
                    },
                    modifier = Modifier.fillMaxWidth().height(47.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LostFoundBlue),
                ) {
                    Text("Continue to private evidence", fontSize = 12.sp)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null,
                        modifier = Modifier.size(17.dp))
                }
            } else {
                Text("Record details that only authorised staff should see. This preview discards them when you leave.",
                    color = LostFoundMuted, fontSize = 11.sp, lineHeight = 16.sp)
                CheckInField("Hidden identifying marks", privateEvidence,
                    { privateEvidence = it }, "e.g. an internal stitched label", false)
                CheckInField("Condition and handling notes", condition,
                    { condition = it }, "e.g. dry, intact, stored in a sealed bag", false)
                SafetyNotice("No custody record or matching job is created in this prototype.")
                Button(
                    onClick = onBackToOffice,
                    modifier = Modifier.fillMaxWidth().height(47.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LostFoundBlue),
                ) { Text("Finish preview · discard details", fontSize = 12.sp) }
                TextButton(onClick = { step = 1 }, modifier = Modifier.fillMaxWidth()) {
                    Text("Back to receipt details", color = LostFoundBlue, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun CategorySelector(value: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text("Category", color = LostFoundNavy, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        Box {
            Card(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, LostFoundBorder),
            ) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 13.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(value, color = LostFoundNavy, fontSize = 12.sp)
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Choose category",
                        tint = LostFoundMuted, modifier = Modifier.size(18.dp))
                }
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                listOf("Bags", "Electronics", "Keys", "Clothing", "Documents", "Other")
                    .forEach { option ->
                        DropdownMenuItem(text = { Text(option) }, onClick = {
                            onSelect(option)
                            expanded = false
                        })
                    }
            }
        }
    }
}

@Composable
private fun CheckInField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isError: Boolean,
) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(label, color = LostFoundNavy, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = LostFoundMuted, fontSize = 11.sp) },
            minLines = 1,
            maxLines = 3,
            isError = isError,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = LostFoundBlue,
                unfocusedBorderColor = LostFoundBorder,
            ),
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp, color = LostFoundNavy),
        )
        if (isError) Text("Required", color = Color(0xFFB42318), fontSize = 10.sp)
    }
}

@Composable
private fun SafetyNotice(message: String) {
    Row(
        Modifier.fillMaxWidth().background(Color(0xFFEFF6FF), RoundedCornerShape(9.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(9.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(Icons.Default.Shield, contentDescription = null, tint = LostFoundBlue,
            modifier = Modifier.size(18.dp))
        Text(message, color = LostFoundNavy, fontSize = 11.sp, lineHeight = 15.sp)
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun FoundItemCheckInPreview() {
    App_1Theme { FoundItemCheckInScreen({}) }
}
