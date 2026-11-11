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
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
fun NotificationsScreen(
    onOpenMatch: (String) -> Unit,
    onOpenClaim: () -> Unit,
    onOpenReports: () -> Unit,
    modifier: Modifier = Modifier,
    readMask: Int = 0,
    onReadMaskChange: (Int) -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Top Header Row
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
                Text(
                    text = "Notifications",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF0F2942),
                )
            }

            TextButton(onClick = { onReadMaskChange(0b111) }) {
                Text(
                    text = "Mark all read",
                    color = Color(0xFF1D4ED8),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        HorizontalDivider(color = Color(0xFFE2E8F0))

        // Notifications List Scrollable
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("SAMPLE NOTIFICATIONS · NO LIVE UPDATES", color = Color(0xFF475569), fontSize = 11.sp)
            NotificationCard(
                title = "New match candidate found",
                description = "A found bag matching 'Navy backpack' is ready for review.",
                timeAgo = "Sample",
                isUnread = readMask and 1 == 0,
                onClick = { onReadMaskChange(readMask or 1); onOpenMatch("navy-backpack") },
            )

            NotificationCard(
                title = "Claim status updated",
                description = "See how office review would work for 'Navy backpack'. No claim has been sent.",
                timeAgo = "Sample",
                isUnread = readMask and 2 == 0,
                onClick = { onReadMaskChange(readMask or 2); onOpenClaim() },
            )

            NotificationCard(
                title = "Welcome to Strathmore Lost & Found",
                description = "Explore sample reports and locally saved drafts in this prototype.",
                timeAgo = "Sample",
                isUnread = readMask and 4 == 0,
                onClick = { onReadMaskChange(readMask or 4); onOpenReports() },
            )
        }
    }
}

@Composable
private fun NotificationCard(
    title: String,
    description: String,
    timeAgo: String,
    isUnread: Boolean,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnread) Color(0xFFEFF6FF) else Color.White,
        ),
        border = BorderStroke(1.dp, if (isUnread) Color(0xFFBFDBFE) else Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isUnread) Color(0xFF1D4ED8) else Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = if (isUnread) Color.White else Color(0xFF475569),
                    modifier = Modifier.size(18.dp),
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF0F2942),
                    )
                    if (isUnread) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF1D4ED8),
                            modifier = Modifier.size(8.dp),
                        ) {}
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color(0xFF475569),
                    lineHeight = 16.sp,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = timeAgo,
                    fontSize = 10.sp,
                    color = Color(0xFF475569),
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Notifications View")
@Composable
fun NotificationsPreview() {
    App_1Theme {
        NotificationsScreen(onOpenMatch = {}, onOpenClaim = {}, onOpenReports = {})
    }
}
