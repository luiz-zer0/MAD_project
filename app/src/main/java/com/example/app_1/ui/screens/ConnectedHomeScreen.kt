package com.example.app_1.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val navy = Color(0xFF112B4B)
private val blue = Color(0xFF245ECA)
private val muted = Color(0xFF697586)

@Composable
internal fun ConnectedHomeScreen(
    email: String,
    role: String,
    reports: List<JSONObject>,
    backendOnline: Boolean,
    onLost: () -> Unit,
    onFound: () -> Unit,
    onReports: () -> Unit,
    onMatches: () -> Unit,
    onProfile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val firstName = email.substringBefore('@').substringBefore('.').replaceFirstChar { it.uppercase() }
    val initials = email.substringBefore('@').split('.', '_', '-').take(2)
        .mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("")
    val greeting = when (java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)) {
        in 0..11 -> "Good morning"
        in 12..17 -> "Good afternoon"
        else -> "Good evening"
    }
    val latest = reports.firstOrNull()
    Column(modifier.fillMaxSize().background(Color(0xFFF8F7F2))) {
        Row(Modifier.fillMaxWidth().background(Color.White).height(64.dp).padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.size(width = 36.dp, height = 40.dp).background(navy, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center) {
                Text("S", color = Color(0xFFC69A3C), fontSize = 26.sp)
            }
            Column(Modifier.weight(1f)) {
                Text("Lost & Found", color = navy, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("STRATHMORE UNIVERSITY", color = muted, fontSize = 10.sp)
            }
            TextButton(onClick = onProfile, contentPadding = PaddingValues(horizontal = 2.dp)) {
                Text(initials.ifBlank { "ME" }, color = blue, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
        HorizontalDivider(color = Color(0xFFDEE3E9))
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(Date()),
                    color = muted, fontSize = 12.sp)
                Text("$greeting, $firstName.", color = navy, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(if (role == "student") "Student" else if (role == "office_staff") "Office staff" else "Administrator",
                    color = blue, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            Card(shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = navy), modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth().height(162.dp).padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                        Text("• OUR CAMPUS. OUR COMMUNITY.", color = Color(0xFFE6C67E),
                            fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                        Text("A little help.\nA happy reunion.", color = Color.White,
                            fontSize = 20.sp, lineHeight = 23.sp)
                        Text("Find what matters, with a little help from Strathmore.",
                            color = Color(0xFFCDDAEA), fontSize = 11.sp, lineHeight = 16.sp)
                    }
                    BackpackIllustration(82)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                HomeAction("I lost something", "Let's help you find it", true, onLost, Modifier.weight(1f))
                HomeAction("I found something", "Help it get home", false, onFound, Modifier.weight(1f))
            }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Your latest update", color = navy, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                TextButton(onClick = onReports) { Text("View all", color = blue, fontSize = 12.sp) }
            }
            Card(onClick = if (latest != null) onMatches else onReports,
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFFDEE3E9)),
                colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    BackpackIllustration(52)
                    Column(Modifier.weight(1f)) {
                        Text(if (latest != null) latest.optString("item_name") else if (backendOnline) "No reports yet" else "Reports unavailable",
                            color = navy, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Text(if (latest != null) "Review your report and possible matches" else if (backendOnline)
                            "Start by reporting something you lost" else "Your sign-in worked. Try again later for updates.",
                            color = muted, fontSize = 12.sp, lineHeight = 17.sp)
                    }
                    Icon(Icons.Default.ChevronRight, null, tint = muted)
                }
            }
            Row(Modifier.fillMaxWidth().background(Color(0xFFFBF2DD), RoundedCornerShape(10.dp)).padding(12.dp),
                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(Icons.Default.Home, null, tint = Color(0xFF8B6C20), modifier = Modifier.size(22.dp))
                Column {
                    Text("Lost & Found Office", color = navy, fontSize = 12.sp)
                    Text("Student Centre · Check current opening hours", color = muted, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun BackpackIllustration(size: Int) {
    val scale = size / 88f
    Box(Modifier.size(size.dp).background(Color(0xFFEDF3FE), RoundedCornerShape(14.dp))) {
        Box(Modifier.offset(x = (34 * scale).dp, y = (13 * scale).dp)
            .size((20 * scale).dp, (16 * scale).dp)
            .border((3 * scale).dp, navy, RoundedCornerShape((6 * scale).dp)))
        Box(Modifier.offset(x = (21 * scale).dp, y = (22 * scale).dp)
            .size((46 * scale).dp, (51 * scale).dp)
            .background(navy, RoundedCornerShape((11 * scale).dp)))
        Box(Modifier.offset(x = (27 * scale).dp, y = (47 * scale).dp)
            .size((34 * scale).dp, (20 * scale).dp)
            .background(Color(0xFF31567D), RoundedCornerShape((5 * scale).dp)))
        Box(Modifier.offset(x = (30 * scale).dp, y = (51 * scale).dp)
            .size((28 * scale).dp, (2 * scale).dp)
            .background(Color(0xFFC69A3C)))
    }
}

@Composable
private fun HomeAction(title: String, subtitle: String, primary: Boolean, onClick: () -> Unit, modifier: Modifier) {
    Card(onClick = onClick, modifier = modifier.height(112.dp), shape = RoundedCornerShape(16.dp),
        border = if (primary) null else BorderStroke(1.dp, Color(0xFFDEE3E9)),
        colors = CardDefaults.cardColors(containerColor = if (primary) blue else Color.White)) {
        Column(Modifier.fillMaxSize().padding(14.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Icon(if (primary) Icons.Default.Search else Icons.Default.Favorite, null,
                tint = if (primary) Color.White else blue, modifier = Modifier.size(23.dp))
            Column {
                Text(title, color = if (primary) Color.White else navy, fontSize = 13.sp)
                Text(subtitle, color = if (primary) Color(0xFFDAE6FF) else muted, fontSize = 10.sp)
            }
        }
    }
}

@Composable
internal fun ConnectedBottomNav(page: String, role: String, onSelect: (String) -> Unit) {
    val destinations = if (role == "student") listOf(
        Triple("Home", Icons.Default.Home, "Home"),
        Triple("Reports", Icons.Default.List, "Reports"),
        Triple("Matches", Icons.Default.Search, "Matches"),
        Triple("Alerts", Icons.Default.Notifications, "Alerts"),
    ) else if (role == "office_staff") listOf(
        Triple("Office", Icons.Default.Home, "Office"),
        Triple("Inventory", Icons.Default.List, "Inventory"),
        Triple("Claims", Icons.Default.CheckCircle, "Claims"),
        Triple("Profile", Icons.Default.Person, "Account"),
    ) else listOf(
        Triple("Admin", Icons.Default.Home, "Admin"),
        Triple("Rules", Icons.Default.List, "Rules"),
        Triple("Audit", Icons.Default.CheckCircle, "Audit"),
        Triple("Profile", Icons.Default.Person, "Account"),
    )
    NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
        destinations.forEach { (destination, icon, label) ->
            NavigationBarItem(selected = page == destination,
                onClick = { onSelect(destination) },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label, fontSize = 11.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = blue, selectedTextColor = blue,
                    unselectedIconColor = muted, unselectedTextColor = muted,
                    indicatorColor = Color(0xFFEDF3FE)))
        }
    }
}
