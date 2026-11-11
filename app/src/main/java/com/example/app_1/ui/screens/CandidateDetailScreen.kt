package com.example.app_1.ui.screens

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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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

/** Public candidate details only. Private identifiers are deliberately absent. */
data class CandidateUiModel(
    val id: String,
    val title: String,
    val foundDate: String,
    val location: String,
)

val sampleCandidates = listOf(
    CandidateUiModel("navy-backpack", "Navy backpack", "Found 14 Oct 2026", "Library · study area"),
    CandidateUiModel("dark-backpack", "Dark backpack", "Found 15 Oct 2026", "Student Centre"),
)

@Composable
fun CandidateDetailScreen(
    candidate: CandidateUiModel,
    onBack: () -> Unit,
    onNotMine: () -> Unit,
    onPreviewStatus: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Ownership answers remain only in this composition; leaving the screen discards them.
    var hiddenFeature by remember(candidate.id) { mutableStateOf("") }
    var contents by remember(candidate.id) { mutableStateOf("") }
    var showErrors by remember(candidate.id) { mutableStateOf(false) }

    Column(modifier.fillMaxSize().background(LostFoundPageBackground)) {
        ClaimHeader("Candidate details", onBack, "Not mine", onNotMine)
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, LostFoundBorder),
            ) {
                Column(Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(64.dp).background(Color(0xFFE8ECF1), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center,
                        ) { Text("🎒", fontSize = 34.sp) }
                        Spacer(Modifier.width(13.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text("Held by office", fontSize = 10.sp, color = Color(0xFF29704D),
                                modifier = Modifier.background(Color(0xFFE8F4ED), RoundedCornerShape(50))
                                    .padding(horizontal = 8.dp, vertical = 3.dp))
                            Text(candidate.title, color = LostFoundNavy, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                            Text(candidate.foundDate, color = LostFoundMuted, fontSize = 11.sp)
                            Text(candidate.location, color = LostFoundMuted, fontSize = 11.sp)
                        }
                    }
                    Spacer(Modifier.height(9.dp))
                    Text("General details only · unique identifiers hidden", color = LostFoundMuted, fontSize = 10.sp)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = LostFoundBlue, modifier = Modifier.size(17.dp))
                Spacer(Modifier.width(7.dp))
                Text("Is this yours?", color = LostFoundNavy, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(Modifier.width(8.dp))
                Text("PRIVATE · REQUIRED", color = LostFoundBlue, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
            Text(
                "Tell us details only the owner would know. These answers should only be shared with authorised office staff.",
                color = LostFoundMuted, fontSize = 12.sp, lineHeight = 17.sp,
            )
            PrivateAnswerField(
                label = "Describe a hidden identifying feature",
                value = hiddenFeature,
                onValueChange = { hiddenFeature = it },
                placeholder = "e.g. a stitched mark inside the lining",
                isError = showErrors && hiddenFeature.isBlank(),
            )
            PrivateAnswerField(
                label = "Name at least two things inside",
                value = contents,
                onValueChange = { contents = it },
                placeholder = "e.g. a notebook and a flask",
                isError = showErrors && contents.isBlank(),
            )
            ClaimNotice(
                title = "Office review comes first",
                body = "Staff check your answers before approval. A possible match does not release the item.",
            )
            Spacer(Modifier.height(4.dp))
            Button(
                onClick = {
                    showErrors = true
                    if (hiddenFeature.isNotBlank() && contents.isNotBlank()) onPreviewStatus()
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LostFoundBlue),
            ) {
                Text("Preview claim status", fontSize = 13.sp)
                Spacer(Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(17.dp))
            }
            Text(
                "Prototype only: this app has no claim service. Your answers are not sent to the office. " +
                    "If a claim is approved in the future, bring your campus ID for an in-person handover.",
                color = LostFoundMuted, fontSize = 11.sp, lineHeight = 16.sp,
            )
        }
    }
}

@Composable
private fun PrivateAnswerField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isError: Boolean,
) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(label, color = LostFoundNavy, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = LostFoundMuted, fontSize = 12.sp) },
            isError = isError,
            minLines = 1,
            maxLines = 3,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = LostFoundBlue,
                unfocusedBorderColor = LostFoundBorder,
            ),
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp, color = LostFoundNavy),
        )
        if (isError) Text("This answer is required", color = Color(0xFFB42318), fontSize = 11.sp)
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 700)
@Composable
private fun CandidateDetailPreview() {
    App_1Theme { CandidateDetailScreen(sampleCandidates.first(), {}, {}, {}) }
}

@Composable
private fun ClaimNotice(title: String, body: String) {
    Row(
        Modifier.fillMaxWidth().background(Color(0xFFEFF6FF), RoundedCornerShape(9.dp))
            .padding(13.dp),
        horizontalArrangement = Arrangement.spacedBy(9.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(Icons.Default.Shield, contentDescription = null, tint = LostFoundBlue, modifier = Modifier.size(18.dp))
        Column {
            Text(title, color = LostFoundNavy, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            Text(body, color = LostFoundNavy, fontSize = 11.sp, lineHeight = 15.sp)
        }
    }
}

@Composable
private fun ClaimHeader(
    title: String,
    onBack: () -> Unit,
    actionLabel: String? = null,
    onAction: () -> Unit = {},
) {
    Row(
        Modifier.fillMaxWidth().background(Color.White).height(57.dp).padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = LostFoundNavy)
        }
        Text(title, modifier = Modifier.weight(1f), color = LostFoundNavy, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        if (actionLabel != null) TextButton(onClick = onAction) {
            Text(actionLabel, color = LostFoundBlue, fontSize = 11.sp)
        }
    }
    HorizontalDivider(color = LostFoundBorder)
}
