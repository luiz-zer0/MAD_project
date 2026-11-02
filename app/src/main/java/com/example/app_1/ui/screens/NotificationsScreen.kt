package com.example.app_1.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
fun NotificationsScreen(
    readMask: Int,
    onReadMaskChange: (Int) -> Unit,
    hasClaimPreview: Boolean,
    onOpenClaim: () -> Unit,
    onReviewMatches: () -> Unit,
    onOpenReports: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val unreadCount = (0..2).count { readMask and (1 shl it) == 0 }

    Column(modifier.fillMaxSize().background(LostFoundPageBackground)) {
        Row(
            Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                Box(
                    Modifier.size(33.dp).background(LostFoundNavy, RoundedCornerShape(7.dp)),
                    contentAlignment = Alignment.Center,
                ) { Text("S", color = Color(0xFFEAB308), fontSize = 19.sp, fontWeight = FontWeight.Bold) }
                Text("Notifications", color = LostFoundNavy, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }
            TextButton(onClick = { onReadMaskChange(0b111) }) {
                Text("Mark all read", color = LostFoundBlue, fontSize = 11.sp)
            }
        }
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .padding(horizontal = 17.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp),
        ) {
            Text("SAMPLE NOTIFICATIONS · NO LIVE UPDATES", color = LostFoundMuted,
                fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Today", color = LostFoundNavy, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("$unreadCount unread", color = LostFoundBlue, fontSize = 11.sp)
            }
            NotificationCard(
                icon = Icons.Default.Notifications,
                title = if (hasClaimPreview) "Your claim preview is ready" else "How claim review works",
                body = if (hasClaimPreview) "Your answers were not sent. See the example office review steps."
                    else "Office staff check ownership answers before approving collection.",
                action = if (hasClaimPreview) "View claim preview" else null,
                time = "Sample",
                unread = readMask and 1 == 0,
                onClick = {
                    onReadMaskChange(readMask or 1)
                    if (hasClaimPreview) onOpenClaim()
                },
            )
            NotificationCard(
                icon = Icons.Default.Search,
                title = "New match after office check-in",
                body = "A bag added by the office may match your active lost report. Review it.",
                action = "Review matches",
                time = "Sample",
                unread = readMask and 2 == 0,
                onClick = {
                    onReadMaskChange(readMask or 2)
                    onReviewMatches()
                },
            )
            NotificationCard(
                icon = Icons.Default.CheckCircle,
                title = "Keys received. Thank you!",
                body = "The office has the keys you found. Staff will handle ownership checks.",
                action = "View report",
                time = "Sample",
                unread = readMask and 4 == 0,
                onClick = {
                    onReadMaskChange(readMask or 4)
                    onOpenReports()
                },
            )
            Text("Yesterday", color = LostFoundNavy, fontSize = 12.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 7.dp))
            NotificationCard(
                icon = Icons.Default.CheckCircle,
                title = "Your lost report is live",
                body = "Your report stays active as new office items are added.",
                action = "View report",
                time = "Sample",
                unread = false,
                onClick = onOpenReports,
            )
            Row(
                Modifier.fillMaxWidth().background(Color(0xFFEFF6FF), RoundedCornerShape(9.dp))
                    .padding(13.dp),
                horizontalArrangement = Arrangement.spacedBy(9.dp),
            ) {
                Icon(Icons.Default.Shield, contentDescription = null, tint = LostFoundBlue,
                    modifier = Modifier.size(17.dp))
                Text("Updates stay here in the app. Open a notification to review the next step securely.",
                    color = LostFoundNavy, fontSize = 11.sp, lineHeight = 15.sp)
            }
        }
    }
}

@Composable
private fun NotificationCard(
    icon: ImageVector,
    title: String,
    body: String,
    action: String?,
    time: String,
    unread: Boolean,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, LostFoundBorder),
    ) {
        Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(Modifier.size(30.dp).background(Color(0xFFEFF6FF), RoundedCornerShape(7.dp)),
                contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = LostFoundBlue, modifier = Modifier.size(17.dp))
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(title, modifier = Modifier.weight(1f), color = LostFoundNavy, fontSize = 12.sp,
                        fontWeight = FontWeight.Medium)
                    if (unread) Box(Modifier.size(5.dp).background(LostFoundBlue, RoundedCornerShape(50)))
                }
                Text(body, color = LostFoundMuted, fontSize = 11.sp, lineHeight = 15.sp)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    if (action != null) Text("$action  →", color = LostFoundBlue, fontSize = 10.sp)
                    else Spacer(Modifier.width(1.dp))
                    Text(time, color = LostFoundMuted, fontSize = 9.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun NotificationsPreview() {
    App_1Theme { NotificationsScreen(0, {}, false, {}, {}, {}) }
}
