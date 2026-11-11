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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_1.ui.theme.App_1Theme

@Composable
fun SensitiveItemCheckInScreen(
    onConfirmCheckIn: () -> Unit,
    modifier: Modifier = Modifier,
    preview: OfficeCheckInPreview? = null,
) {
    var categoryLabel by rememberSaveable(preview?.category) { mutableStateOf(preview?.let { "${it.category} - ${it.description}" }.orEmpty()) }
    var foundAt by rememberSaveable(preview?.foundAt) { mutableStateOf(preview?.foundAt.orEmpty()) }
    var receivedTime by rememberSaveable { mutableStateOf("") }
    var custodyLocation by rememberSaveable(preview?.custodyLocation) { mutableStateOf(preview?.custodyLocation.orEmpty()) }
    var showErrors by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0F2942)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "S",
                        color = Color(0xFFEAB308),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                    )
                }
                Column {
                    Text(
                        text = if (preview?.category == "Wallet" || preview?.category == "Documents") "Sensitive item check-in" else "Private evidence preview",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color(0xFF0F2942),
                    )
                    Text(
                        text = "STRATHMORE • LOST & FOUND OFFICE",
                        fontSize = 11.sp,
                        color = Color(0xFF475569),
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E7FF)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "LM",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFF1D4ED8),
                )
            }
        }

        HorizontalDivider(color = Color(0xFFF1F5F9))

        // Body Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFFFEF2F2),
            ) {
                Text(
                    text = "RESTRICTED • OFFICE INTAKE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF991B1B),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                )
            }

            Text(
                text = "Seal wallets and documents on receipt. Never include personal details in the public listing.",
                fontSize = 12.sp,
                color = Color(0xFF475569),
                lineHeight = 16.sp,
            )
            if (preview != null) Text("Date found: ${preview.dateFound} · Received by: ${preview.receivedBy}", color = Color(0xFF475569), fontSize = 11.sp)

            // Category Label
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "Category / general label", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F2942))
                OutlinedTextField(
                    value = categoryLabel,
                    onValueChange = { categoryLabel = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF1D4ED8), unfocusedBorderColor = Color(0xFFCBD5E1)),
                )
            }

            // Found At & Received Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "Found at", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F2942))
                    CampusLocationPicker(foundAt, { foundAt = it })
                }

                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "Received date/time", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F2942))
                    OutlinedTextField(
                        value = receivedTime,
                        onValueChange = { receivedTime = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF1D4ED8), unfocusedBorderColor = Color(0xFFCBD5E1)),
                    )
                }
            }

            // Custody Location with Lock Icon
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "Secure custody location", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F2942))
                OutlinedTextField(
                    value = custodyLocation,
                    onValueChange = { custodyLocation = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    trailingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color(0xFF475569)) },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF1D4ED8), unfocusedBorderColor = Color(0xFFCBD5E1)),
                )
            }

            // Private evidence sealed Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color(0xFF1D4ED8), modifier = Modifier.size(16.dp))
                        Text(text = "Private evidence sealed", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F2942))
                    }
                    Text(
                        text = "Sample restricted evidence. No attachments are stored in this prototype.",
                        fontSize = 11.sp,
                        color = Color(0xFF475569),
                    )
                    Text(text = "Restricted evidence management requires the office service.", color = Color(0xFF475569), fontSize = 12.sp)
                }
            }

            // Notice
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF9C3)),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF854D0E), modifier = Modifier.size(16.dp))
                    Text(
                        text = "Leah Mugo • sealed on receipt. Opening evidence requires an authorized office role and is logged.",
                        fontSize = 11.sp,
                        color = Color(0xFF713F12),
                        lineHeight = 15.sp,
                    )
                }
            }

            // Submit Confirm Button
            if (showErrors && (categoryLabel.isBlank() || foundAt.isBlank() || receivedTime.isBlank() || custodyLocation.isBlank())) {
                Text("Complete category, location, received time and custody location.", color = Color(0xFFB42318), fontSize = 11.sp)
            }
            Button(
                onClick = {
                    showErrors = true
                    if (categoryLabel.isNotBlank() && foundAt.isNotBlank() && receivedTime.isNotBlank() && custodyLocation.isNotBlank()) onConfirmCheckIn()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8)),
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Finish check-in preview",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Sensitive Item Check-In View")
@Composable
fun SensitiveItemCheckInPreview() {
    App_1Theme {
        SensitiveItemCheckInScreen(onConfirmCheckIn = {})
    }
}
