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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
fun OfficeDashboardScreen(
    onCheckInFoundItem: () -> Unit,
    onExitPreview: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize().background(LostFoundPageBackground)) {
        Row(
            Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 16.dp, vertical = 13.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                Box(Modifier.size(34.dp).background(LostFoundNavy, RoundedCornerShape(7.dp)),
                    contentAlignment = Alignment.Center) {
                    Text("S", color = Color(0xFFEAB308), fontSize = 19.sp, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("Office dashboard", color = LostFoundNavy, fontSize = 16.sp,
                        fontWeight = FontWeight.Bold)
                    Text("STRATHMORE · LOST & FOUND OFFICE", color = LostFoundMuted, fontSize = 8.sp)
                }
            }
            TextButton(onClick = onExitPreview) {
                Text("Exit", color = LostFoundBlue, fontSize = 11.sp)
            }
        }
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .padding(horizontal = 17.dp, vertical = 15.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp),
        ) {
            Text("OFFICE WORKFLOW PREVIEW · SAMPLE DATA", color = LostFoundMuted,
                fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(13.dp),
                colors = CardDefaults.cardColors(containerColor = LostFoundNavy),
            ) {
                Column(Modifier.padding(15.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text("CARE. CUSTODY. COMMUNITY.", color = Color(0xFFEAB308),
                        fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text("Every item in safe hands.", color = Color.White, fontSize = 21.sp,
                        fontWeight = FontWeight.Medium)
                    Text("Student Centre · Office open until 5pm", color = Color(0xFFCBD5E1),
                        fontSize = 10.sp)
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                OfficeMetric("24", "In custody", Modifier.weight(1f))
                OfficeMetric("6", "To review", Modifier.weight(1f))
                OfficeMetric("3", "Pickups today", Modifier.weight(1f))
            }
            Button(
                onClick = onCheckInFoundItem,
                modifier = Modifier.fillMaxWidth().height(45.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LostFoundBlue),
            ) {
                Text("Check in a found item", fontSize = 12.sp)
                Spacer(Modifier.width(7.dp))
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Needs your attention", color = LostFoundNavy, fontSize = 15.sp,
                    fontWeight = FontWeight.Bold)
                Text("Claims queue · preview", color = LostFoundBlue, fontSize = 10.sp)
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, LostFoundBorder),
            ) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(Modifier.size(39.dp).background(Color(0xFFEFF6FF), RoundedCornerShape(9.dp)),
                            contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Inventory2, contentDescription = null,
                                tint = LostFoundNavy, modifier = Modifier.size(21.dp))
                        }
                        Column {
                            Text("Navy backpack", color = LostFoundNavy, fontSize = 12.sp,
                                fontWeight = FontWeight.Bold)
                            Text("Sample claim awaiting review", color = LostFoundMuted, fontSize = 10.sp)
                        }
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Held · Shelf A / Bin 3", color = Color(0xFF29704D), fontSize = 9.sp,
                            modifier = Modifier.background(Color(0xFFE8F4ED), RoundedCornerShape(50))
                                .padding(horizontal = 7.dp, vertical = 4.dp))
                        Text("General details only", color = LostFoundMuted, fontSize = 9.sp)
                    }
                }
            }
            Row(
                Modifier.fillMaxWidth().background(Color(0xFFFFF4DC), RoundedCornerShape(10.dp))
                    .padding(13.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("2 items due for retention review", color = LostFoundNavy, fontSize = 11.sp)
                Text("Sample", color = Color(0xFF9A6700), fontSize = 10.sp)
            }
            Spacer(Modifier.height(10.dp))
            Row(
                Modifier.fillMaxWidth().background(Color(0xFFEFF6FF), RoundedCornerShape(9.dp))
                    .padding(13.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(Icons.Default.Shield, contentDescription = null, tint = LostFoundBlue,
                    modifier = Modifier.size(18.dp))
                Text("Office review and ownership questions come first. Staff must record every physical handover.",
                    color = LostFoundNavy, fontSize = 11.sp, lineHeight = 15.sp)
            }
        }
    }
}

@Composable
private fun OfficeMetric(value: String, label: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, LostFoundBorder),
    ) {
        Column(Modifier.padding(10.dp)) {
            Text(value, color = LostFoundNavy, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(label, color = LostFoundMuted, fontSize = 9.sp)
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun OfficeDashboardPreview() {
    App_1Theme { OfficeDashboardScreen({}, {}) }
}
