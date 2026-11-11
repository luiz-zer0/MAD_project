package com.example.app_1.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
fun AccessAndAuditScreen(
    onExportLog: () -> Unit,
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
        // Admin Top Bar
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
                        text = "Access & audit",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF0F2942),
                    )
                    Text(
                        text = "STRATHMORE • TECHNICAL ADMIN",
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
                    text = "RN",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFF1D4ED8),
                )
            }
        }

        // Segmented Tabs
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFE2E8F0).copy(alpha = 0.6f),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                AuditTabPill("Access & roles", selectedTab == 0) { selectedTab = 0 }
                AuditTabPill("Audit activity", selectedTab == 1) { selectedTab = 1 }
            }
        }

        // Security Notice Banner
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
                Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = Color(0xFF1D4ED8), modifier = Modifier.size(16.dp))
                Text(
                    text = "Technical administration does not grant private evidence access. Audit shows event metadata only.",
                    fontSize = 11.sp,
                    color = Color(0xFF1E40AF),
                    lineHeight = 15.sp,
                )
            }
        }

        // Body Content Scrollable
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Permission Boundaries
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "Permission boundaries", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F2942))
                TextButton(onClick = { /* Manage */ }) {
                    Text(text = "Manage", color = Color(0xFF1D4ED8), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    RoleBoundaryRow(
                        name = "Office reviewer • Leah Mugo",
                        badge = "Office",
                        badgeColor = Color(0xFFEFF6FF),
                        badgeTextColor = Color(0xFF1D4ED8),
                        description = "Custody, restricted review & handover",
                    )

                    HorizontalDivider(color = Color(0xFFF1F5F9))

                    RoleBoundaryRow(
                        name = "Technical admin • Rina Nali",
                        badge = "Admin",
                        badgeColor = Color(0xFFF1F5F9),
                        badgeTextColor = Color.Gray,
                        description = "Rules, roles & audit - no claim evidence",
                    )
                }
            }

            // Recent Audit Activity
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "Recent audit activity", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F2942))
                TextButton(onClick = { /* View all */ }) {
                    Text(text = "View all", color = Color(0xFF1D4ED8), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    AuditActivityItem(title = "Handover recorded", subtitle = "Leah Mugo • 15 Oct, 11:18am • Bag record")
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    AuditActivityItem(title = "Restricted review opened", subtitle = "Leah Mugo • 15 Oct, 9:45am • Access logged")
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    AuditActivityItem(title = "Category rule updated", subtitle = "Rina Nali • 14 Oct, 4:20pm • Retention setting")
                }
            }

            // Export Log Button
            Button(
                onClick = onExportLog,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9), contentColor = Color(0xFF0F2942)),
            ) {
                Text(text = "Export redacted audit log", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            Text(
                text = "Role changes require supervisor approval. Evidence views and physical handovers are always logged.",
                fontSize = 10.sp,
                color = Color.Gray,
            )
        }
    }
}

@Composable
private fun AuditTabPill(
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
private fun RoleBoundaryRow(
    name: String,
    badge: String,
    badgeColor: Color,
    badgeTextColor: Color,
    description: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0F2942))
            Surface(shape = RoundedCornerShape(4.dp), color = badgeColor) {
                Text(text = badge, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = badgeTextColor, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
            }
        }
        Text(text = description, fontSize = 11.sp, color = Color.Gray)
    }
}

@Composable
private fun AuditActivityItem(
    title: String,
    subtitle: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(text = title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0F2942))
        Text(text = subtitle, fontSize = 11.sp, color = Color.Gray)
    }
}

@Preview(showBackground = true, name = "Access & Audit View")
@Composable
fun AccessAndAuditPreview() {
    App_1Theme {
        AccessAndAuditScreen(onExportLog = {})
    }
}
