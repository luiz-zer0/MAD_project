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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
fun ClaimsQueueScreen(
    onOpenVerification: () -> Unit,
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
                        text = "Claims queue",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF0F2942),
                    )
                    Text(
                        text = "STRATHMORE • LOST & FOUND OFFICE",
                        fontSize = 9.sp,
                        color = Color.Gray,
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
                QueueTabPill("To review • 6", selectedTab == 0) { selectedTab = 0 }
                QueueTabPill("Follow-up • 2", selectedTab == 1) { selectedTab = 1 }
                QueueTabPill("Approved", selectedTab == 2) { selectedTab = 2 }
            }
        }

        Text(
            text = "Compare private ownership answers with office evidence. A possible match is not proof.",
            fontSize = 12.sp,
            color = Color.Gray,
            lineHeight = 16.sp,
        )

        // Subtitle & Sort
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "Awaiting a person's review", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F2942))
            TextButton(onClick = { /* Sort */ }) {
                Text(text = "Oldest first", color = Color(0xFF1D4ED8), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // List Scrollable
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Claim Card 1
            ClaimQueueCard(
                title = "Navy backpack",
                tagLabel = "New claim",
                tagColor = Color(0xFFFEF9C3),
                tagTextColor = Color(0xFF854D0E),
                claimant = "Mara Njeri • Today, 9:35am",
                location = "Held • Student Centre / Shelf A / Bin 3",
                onOpenReview = onOpenVerification,
            )

            // Claim Card 2
            ClaimQueueCard(
                title = "Brown wallet",
                tagLabel = "Sensitive item",
                tagColor = Color(0xFFFEF2F2),
                tagTextColor = Color(0xFF991B1B),
                claimant = "Eli Otieno • Today, 9:38am",
                location = "Sealed • Student Centre / Safe / Pouch W07",
                onOpenReview = onOpenVerification,
            )

            // Notice Banner
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
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF1D4ED8), modifier = Modifier.size(16.dp))
                    Text(
                        text = "Ownership questions and human office review are required before approval. No automatic release.",
                        fontSize = 11.sp,
                        color = Color(0xFF1E40AF),
                        lineHeight = 15.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun QueueTabPill(
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
                color = if (isSelected) Color(0xFF0F2942) else Color.Gray,
            )
        }
    }
}

@Composable
private fun ClaimQueueCard(
    title: String,
    tagLabel: String,
    tagColor: Color,
    tagTextColor: Color,
    claimant: String,
    location: String,
    onOpenReview: () -> Unit,
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
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF0F2942),
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = tagColor,
                ) {
                    Text(
                        text = tagLabel,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = tagTextColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(text = claimant, fontSize = 12.sp, color = Color.Gray)
            Text(text = location, fontSize = 11.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(4.dp))

            TextButton(
                onClick = onOpenReview,
                modifier = Modifier.align(Alignment.Start),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Open office review", color = Color(0xFF1D4ED8), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color(0xFF1D4ED8),
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Claims Queue View")
@Composable
fun ClaimsQueuePreview() {
    App_1Theme {
        ClaimsQueueScreen(onOpenVerification = {})
    }
}
