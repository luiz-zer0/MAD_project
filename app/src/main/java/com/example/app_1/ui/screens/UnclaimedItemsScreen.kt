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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
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
fun UnclaimedItemsScreen(
    onSendPlanForReview: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // Staff Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
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
                        text = "Unclaimed items",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
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

        // Filter Pills Row
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFE2E8F0).copy(alpha = 0.6f),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                UnclaimedTabPill("Due for review • 2", selectedTab == 0) { selectedTab = 0 }
                UnclaimedTabPill("On hold", selectedTab == 1) { selectedTab = 1 }
                UnclaimedTabPill("Resolved", selectedTab == 2) { selectedTab = 2 }
            }
        }

        // Notice Banner
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
                Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = Color(0xFF854D0E), modifier = Modifier.size(16.dp))
                Text(
                    text = "Retention review is not disposal approval. Check open claims and record an authorized decision.",
                    fontSize = 11.sp,
                    color = Color(0xFF713F12),
                    lineHeight = 15.sp,
                )
            }
        }

        // List Scrollable
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (selectedTab == 0) {
            // Item 1
            UnclaimedItemCard(
                title = "Canvas tote",
                statusTag = "Review due",
                subtitle = "Received 15 Sep • 30-day review due today\nStudent Centre • Shelf B / Bin 1\nHeld • no active claim",
            )

            // Item 2
            UnclaimedItemCard(
                title = "Document folder",
                statusTag = "Review due",
                subtitle = "Received 15 Sep • Secure handling required\nStudent Centre • Safe / Pouch D03\nSealed • private evidence restricted",
            )

            // Review Plan Card
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
                    Text(text = "Review plan • canvas tote", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F2942))
                    Text(
                        text = "No open claim found. Propose transfer to approved campus storage; supervisor sign-off required.",
                        fontSize = 11.sp,
                        color = Color(0xFF475569),
                        lineHeight = 15.sp,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Pending sign-off - remains in office custody",
                        fontSize = 10.sp,
                        color = Color(0xFFD97706),
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            } else {
                Text("No ${if (selectedTab == 1) "items on hold" else "resolved items"} in this preview.", color = Color(0xFF475569))
            }
        }

        Text("Supervisor review needs a connected office service. No plan is sent from this preview.", color = Color(0xFF475569), fontSize = 12.sp)

        Text(
            text = "A staff-recorded transfer log is required when moved.",
            fontSize = 10.sp,
            color = Color(0xFF475569),
        )
    }
}

@Composable
private fun UnclaimedTabPill(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) Color.White else Color.Transparent,
        modifier = Modifier,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
        ) {
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color(0xFF0F2942) else Color(0xFF475569),
            )
        }
    }
}

@Composable
private fun UnclaimedItemCard(
    title: String,
    statusTag: String,
    subtitle: String,
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F2942))
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFFFEF9C3),
                ) {
                    Text(
                        text = statusTag,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF854D0E),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = subtitle, fontSize = 11.sp, color = Color(0xFF475569), lineHeight = 15.sp)
        }
    }
}

@Preview(showBackground = true, name = "Unclaimed Items View")
@Composable
fun UnclaimedItemsPreview() {
    App_1Theme {
        UnclaimedItemsScreen(onSendPlanForReview = {})
    }
}
