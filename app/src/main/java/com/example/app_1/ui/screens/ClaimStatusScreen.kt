package com.example.app_1.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
fun ClaimStatusScreen(
    candidate: CandidateUiModel,
    onBack: () -> Unit,
    onBackToReports: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize().background(LostFoundPageBackground)) {
        ClaimHeader("Claim status", onBack)
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = LostFoundNavy),
            ) {
                Column(Modifier.padding(17.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("PREVIEW · NOT SUBMITTED", color = LostFoundNavy, fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.background(Color(0xFFFFE9BD), RoundedCornerShape(50))
                            .padding(horizontal = 9.dp, vertical = 4.dp))
                    Text(candidate.title, color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                    Text("No claim has been sent to the office", color = Color(0xFFCBD5E1), fontSize = 11.sp)
                }
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, LostFoundBorder),
            ) {
                Column(Modifier.padding(15.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    ClaimStep(Icons.Default.CheckCircle, Color(0xFF25845C),
                        "Ownership answers entered", "Your private answers have not been transmitted.")
                    HorizontalDivider(color = LostFoundBorder)
                    ClaimStep(Icons.Default.Schedule, Color(0xFFAA7C17),
                        "Office review", "Will begin only after a secure claim service is connected.")
                    HorizontalDivider(color = LostFoundBorder)
                    ClaimStep(Icons.Default.Lock, LostFoundMuted,
                        "Collection after approval", "Staff approval and an in-person ID check are required.")
                }
            }
            ClaimNotice(
                title = "No automatic release",
                body = "Please wait for staff approval before visiting. A possible match alone is not proof of ownership.",
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, LostFoundBorder),
            ) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Storefront, contentDescription = null, tint = LostFoundBlue, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(11.dp))
                    Column {
                        Text("Lost & Found Office", color = LostFoundNavy, fontSize = 12.sp)
                        Text("Student Centre · Mon–Fri, 8am–5pm", color = LostFoundMuted, fontSize = 10.sp)
                    }
                }
            }
            Spacer(Modifier.height(3.dp))
            Button(
                onClick = onBackToReports,
                modifier = Modifier.fillMaxWidth().height(45.dp),
                shape = RoundedCornerShape(9.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = LostFoundNavy),
                border = BorderStroke(1.dp, LostFoundBorder),
            ) { Text("Back to my reports", fontSize = 12.sp) }
        }
    }
}

@Composable
private fun ClaimStep(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    title: String,
    body: String,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
        Column {
            Text(title, color = LostFoundNavy, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text(body, color = LostFoundMuted, fontSize = 10.sp, lineHeight = 15.sp)
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 700)
@Composable
private fun ClaimStatusPreview() {
    App_1Theme { ClaimStatusScreen(sampleCandidates.first(), {}, {}) }
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
