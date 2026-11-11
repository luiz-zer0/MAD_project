package com.example.app_1.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
fun FoundItemCheckInScreen(
    onContinueToEvidence: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var category by rememberSaveable { mutableStateOf("Bags") }
    var description by rememberSaveable { mutableStateOf("Navy backpack with a front zip pocket.") }
    var dateFound by rememberSaveable { mutableStateOf("14 Oct 2026") }
    var locationFound by rememberSaveable { mutableStateOf("Library • study area") }
    var custodyLocation by rememberSaveable { mutableStateOf("Student Centre • Shelf A / Bin 3") }
    var receivedBy by rememberSaveable { mutableStateOf("Leah Mugo • Received at office, 8:55am") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        // Top App Bar
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
                Text(
                    text = "Found item check-in",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF0F2942),
                )
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

        // Form Body Scrollable
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Step Header
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "STEP 1 OF 2 • RECEIPT & CUSTODY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1D4ED8),
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFF1D4ED8)),
                )
            }

            Text(
                text = "Receive the item in person. On check-in, active lost reports are checked for new matches.",
                fontSize = 12.sp,
                color = Color.Gray,
                lineHeight = 16.sp,
            )

            // Category Field
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "Category", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F2942))
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    trailingIcon = { Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null) },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF1D4ED8), unfocusedBorderColor = Color(0xFFCBD5E1)),
                )
            }

            // Description Field
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "General description", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F2942))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF1D4ED8), unfocusedBorderColor = Color(0xFFCBD5E1)),
                )
            }

            // Dates & Found location Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "Date found", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F2942))
                    OutlinedTextField(
                        value = dateFound,
                        onValueChange = { dateFound = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        trailingIcon = { Icon(imageVector = Icons.Default.DateRange, contentDescription = null, tint = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF1D4ED8), unfocusedBorderColor = Color(0xFFCBD5E1)),
                    )
                }

                Column(modifier = Modifier.weight(1.2f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "Found at", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F2942))
                    OutlinedTextField(
                        value = locationFound,
                        onValueChange = { locationFound = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        trailingIcon = { Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF1D4ED8), unfocusedBorderColor = Color(0xFFCBD5E1)),
                    )
                }
            }

            // Custody Location
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "Custody location", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F2942))
                OutlinedTextField(
                    value = custodyLocation,
                    onValueChange = { custodyLocation = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    trailingIcon = { Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF1D4ED8), unfocusedBorderColor = Color(0xFFCBD5E1)),
                )
            }

            // Received By
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "Received by / physical status", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F2942))
                OutlinedTextField(
                    value = receivedBy,
                    onValueChange = { receivedBy = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF1D4ED8), unfocusedBorderColor = Color(0xFFCBD5E1)),
                )
            }

            // Privacy Notice Box
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF1D4ED8),
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = "Keep hidden marks, contents and personal details out of general descriptions and photos.",
                        fontSize = 11.sp,
                        color = Color(0xFF1E40AF),
                        lineHeight = 15.sp,
                    )
                }
            }

            // Submit Button
            Button(
                onClick = onContinueToEvidence,
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
                        text = "Continue to private evidence",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Found Item Check-In View")
@Composable
fun FoundItemCheckInPreview() {
    App_1Theme {
        FoundItemCheckInScreen(onContinueToEvidence = {})
    }
}
