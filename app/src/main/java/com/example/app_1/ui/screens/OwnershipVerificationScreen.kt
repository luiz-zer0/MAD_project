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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
fun OwnershipVerificationScreen(
    onApprovePickup: () -> Unit,
    modifier: Modifier = Modifier,
    onKeepOnHold: () -> Unit = {},
) {
    var isConfirmed by rememberSaveable { mutableStateOf(true) }

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
                        text = "Ownership verification",
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

        // Body Content Scrollable
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Restricted Banner
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
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color(0xFF854D0E), modifier = Modifier.size(16.dp))
                    Text(
                        text = "Sample comparison only. No staff authorization or evidence access is performed or logged by this prototype.",
                        fontSize = 11.sp,
                        color = Color(0xFF713F12),
                        lineHeight = 15.sp,
                    )
                }
            }

            // Header Banner Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2942)),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFEAB308).copy(alpha = 0.2f),
                    ) {
                        Text(
                            text = "SAMPLE REVIEW · NO DECISION",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEAB308),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        )
                    }
                    Text(
                        text = "Navy backpack • Mara Njeri",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                    Text(
                        text = "Held • Student Centre / Shelf A / Bin 3",
                        fontSize = 12.sp,
                        color = Color(0xFFCBD5E1),
                    )
                }
            }

            // Answers Comparison Table
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    // Q1
                    Text(
                        text = "What hidden identifying feature is inside?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF0F2942),
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        ComparisonCell(label = "CLAIMANT ANSWER", text = "Small stitched star inside the lining.", modifier = Modifier.weight(1f))
                        ComparisonCell(label = "OFFICE OBSERVATION", text = "Stitched star seen inside the lining.", modifier = Modifier.weight(1f))
                    }

                    HorizontalDivider(color = Color(0xFFF1F5F9))

                    // Q2
                    Text(
                        text = "Name at least two things inside.",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF0F2942),
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        ComparisonCell(label = "CLAIMANT ANSWER", text = "A green notebook and a silver flask.", modifier = Modifier.weight(1f))
                        ComparisonCell(label = "OFFICE OBSERVATION", text = "Green notebook and silver flask present.", modifier = Modifier.weight(1f))
                    }
                }
            }

            // Checkbox Confirmation
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Checkbox(
                    checked = isConfirmed,
                    onCheckedChange = { isConfirmed = it },
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF1D4ED8)),
                )
                Text(
                    text = "I compared both ownership answers with the physical item. Leah Mugo • Office reviewer",
                    fontSize = 11.sp,
                    color = Color(0xFF0F2942),
                    lineHeight = 15.sp,
                )
            }

            // Approve Button
            Button(
                onClick = onApprovePickup,
                enabled = isConfirmed,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8)),
            ) {
                Text(text = "Preview pickup steps", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            // Hold Button
            OutlinedButton(
                onClick = onKeepOnHold,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
            ) {
                Text(text = "Return to claims queue", color = Color(0xFF0F2942), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }

            Text(
                text = "Approval is a handover decision, not a release. Staff must check identity and record physical handover.",
                fontSize = 11.sp,
                color = Color(0xFF475569),
                lineHeight = 15.sp,
            )
        }
    }
}

@Composable
private fun ComparisonCell(
    label: String,
    text: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFF8FAFC),
        modifier = modifier,
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D4ED8))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = text, fontSize = 11.sp, color = Color(0xFF0F2942), lineHeight = 15.sp)
        }
    }
}

@Preview(showBackground = true, name = "Ownership Verification View")
@Composable
fun OwnershipVerificationPreview() {
    App_1Theme {
        OwnershipVerificationScreen(onApprovePickup = {})
    }
}
