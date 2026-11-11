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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_1.R
import com.example.app_1.ui.theme.App_1Theme

private data class InventoryPreviewItem(
    val title: String,
    val subtitle: String,
    val custodyLocation: String,
    val privacyBadge: String,
    val isSealed: Boolean,
)

@Composable
fun InventoryScreen(
    onSelectItem: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterTab by remember { mutableIntStateOf(0) }
    var selectedItemIndex by remember { mutableIntStateOf(-1) }
    val sampleItems = listOf(
        InventoryPreviewItem("Navy backpack", "Found 14 Oct • Library study area", "Shelf A / Bin 3 • Held", "General details only", false),
        InventoryPreviewItem("Brown wallet", "Found 15 Oct • Cafeteria", "Safe / Pouch W07 • Sealed", "Evidence restricted", true),
        InventoryPreviewItem("Key ring", "Found 15 Oct • Sports Centre", "Key cabinet / Hook K2 • Held", "General details only", false),
    )
    val visibleItems = if (selectedFilterTab == 0) sampleItems.withIndex().filter { (_, item) ->
        listOf(item.title, item.subtitle, item.custodyLocation).any { it.contains(searchQuery, ignoreCase = true) }
    } else emptyList()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        if (selectedItemIndex >= 0) {
            val item = sampleItems[selectedItemIndex]
            AlertDialog(
                onDismissRequest = { selectedItemIndex = -1 },
                title = { Text(item.title) },
                text = { Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(item.subtitle)
                    Text(item.custodyLocation)
                    Text(item.privacyBadge)
                    Text("Sample inventory record. No live custody record is loaded.")
                } },
                confirmButton = {
                    if (selectedItemIndex == 0) TextButton(onClick = {
                        selectedItemIndex = -1
                        onSelectItem()
                    }) { Text("Preview claim review") }
                    else TextButton(onClick = { selectedItemIndex = -1 }) { Text("Close") }
                },
                dismissButton = if (selectedItemIndex == 0) ({ TextButton(onClick = { selectedItemIndex = -1 }) { Text("Close") } }) else null,
            )
        }
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
                        text = "Inventory",
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

        // Search Field
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = "Find an office record", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F2942))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search item or custody location", fontSize = 12.sp, color = Color(0xFF475569)) },
                trailingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color(0xFF475569)) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF0F2942)),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF1D4ED8), unfocusedBorderColor = Color(0xFFCBD5E1)),
            )
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
                FilterPillItem("In custody • 3", selectedFilterTab == 0) { selectedFilterTab = 0 }
                FilterPillItem("Ready", selectedFilterTab == 1) { selectedFilterTab = 1 }
                FilterPillItem("Released", selectedFilterTab == 2) { selectedFilterTab = 2 }
            }
        }

        // Subtitle & Filter
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "Held by office", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F2942))
            Text(text = "Sample records", color = Color(0xFF475569), fontSize = 12.sp)
        }

        // List Scrollable
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            visibleItems.forEach { (index, item) ->
                InventoryItemCard(
                    title = item.title,
                    subtitle = item.subtitle,
                    custodyLocation = item.custodyLocation,
                    privacyBadge = item.privacyBadge,
                    isSealed = item.isSealed,
                    onClick = { selectedItemIndex = index },
                )
            }
            if (visibleItems.isEmpty()) Text("No sample inventory items match this view.", color = Color(0xFF475569))

            Text(
                text = "General details only. Private evidence is available solely in authorized office review.",
                fontSize = 11.sp,
                color = Color(0xFF475569),
                lineHeight = 15.sp,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun FilterPillItem(
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
private fun InventoryItemCard(
    title: String,
    subtitle: String,
    custodyLocation: String,
    privacyBadge: String,
    isSealed: Boolean,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSealed) Color(0xFFFEF9C3) else Color(0xFFEFF6FF)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(if (isSealed) R.drawable.ic_favorite else R.drawable.ic_home),
                    contentDescription = null,
                    tint = if (isSealed) Color(0xFF854D0E) else Color(0xFF1D4ED8),
                    modifier = Modifier.size(22.dp),
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF0F2942),
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = Color(0xFF475569),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFF1F5F9),
                    ) {
                        Text(
                            text = custodyLocation,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF334155),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                        )
                    }
                    Text(
                        text = privacyBadge,
                        fontSize = 11.sp,
                        color = Color(0xFF475569),
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFF475569),
            )
        }
    }
}

@Preview(showBackground = true, name = "Inventory View")
@Composable
fun InventoryPreview() {
    App_1Theme {
        InventoryScreen(onSelectItem = {})
    }
}
