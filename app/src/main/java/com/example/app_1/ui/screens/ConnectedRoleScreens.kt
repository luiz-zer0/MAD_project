package com.example.app_1.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONObject

private val navy = Color(0xFF112B4B)
private val blue = Color(0xFF245ECA)
private val muted = Color(0xFF697586)

@Composable
internal fun ConnectedPageHeader(title: String, subtitle: String = "STRATHMORE UNIVERSITY") {
    Row(Modifier.fillMaxWidth().background(Color.White).height(64.dp).padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(Modifier.size(36.dp, 40.dp).background(navy, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
            Text("S", color = Color(0xFFC69A3C), fontSize = 26.sp)
        }
        Column {
            Text(title, color = navy, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = muted, fontSize = 10.sp)
        }
    }
    HorizontalDivider(color = Color(0xFFDEE3E9))
}

@Composable
internal fun ConnectedNotice(title: String, detail: String) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Color(0xFFDEE3E9)),
        colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Text(title, color = navy, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text(detail, color = muted, fontSize = 13.sp, lineHeight = 19.sp)
        }
    }
}

@Composable
internal fun OfficeDashboard(reports: List<JSONObject>, claims: List<JSONObject>, online: Boolean,
    onCheckIn: () -> Unit, onInventory: () -> Unit, onClaims: () -> Unit, onUnclaimed: () -> Unit) {
    Text("Lost & Found Office", color = navy, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = navy)) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("OUR CAMPUS. OUR COMMUNITY.", color = Color(0xFFE6C67E), fontSize = 11.sp)
            Text("Every item in safe hands.", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Check in found items, review claims, and record each handover.", color = Color(0xFFCDDAEA), fontSize = 13.sp)
        }
    }
    if (online) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OfficeCount("In custody", reports.count { it.optString("report_type") == "found" && it.optString("status") == "active" }, Modifier.weight(1f))
            OfficeCount("To review", claims.count { it.optString("status") == "pending" }, Modifier.weight(1f))
            OfficeCount("Ready", claims.count { it.optString("status") == "approved" }, Modifier.weight(1f))
        }
    } else ConnectedNotice("Office data unavailable", "Connect to the service to see inventory and claims. No counts are shown until they can be verified.")
    Button(onClick = onCheckIn, modifier = Modifier.fillMaxWidth().height(52.dp),
        colors = ButtonDefaults.buttonColors(containerColor = blue), shape = RoundedCornerShape(10.dp)) {
        Text("Check in a found item")
    }
    ConnectedNotice("Claims needing attention", if (online) {
        val count = claims.count { it.optString("status") == "pending" }
        if (count == 0) "No claims are waiting for review." else "$count claim(s) await in-person ownership verification."
    } else "Claims will appear when the office service is available.")
    OutlinedButton(onClick = onClaims, modifier = Modifier.fillMaxWidth()) { Text("Open claims queue") }
    OutlinedButton(onClick = onInventory, modifier = Modifier.fillMaxWidth()) { Text("View inventory") }
    ConnectedNotice("Retention review", "Review items only after a category retention rule is configured. Any disposition needs a separate office record.")
    OutlinedButton(onClick = onUnclaimed, modifier = Modifier.fillMaxWidth()) { Text("Review unclaimed items") }
}

@Composable
internal fun OfficeUnclaimed(items: List<JSONObject>, online: Boolean, policyCount: Int) {
    Text("Unclaimed items", color = navy, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    ConnectedNotice("Retention review", "Items appear only when a configured category rule has reached its retention period. Review custody records before any disposition.")
    if (!online) ConnectedNotice("Review unavailable", "Connect to the office service to load retention candidates.")
    else if (policyCount == 0) ConnectedNotice("No retention rules configured", "A technical administrator must set category rules before items can be flagged for review.")
    else if (items.isEmpty()) ConnectedNotice("Nothing due for review", "No active items have reached a configured retention period.")
    items.forEach { item -> ConnectedNotice(item.optString("item_name"),
        "${item.optInt("age_days")} days in custody · ${item.optInt("retention_days")} day rule · ${item.optString("category_id")}") }
}

@Composable
private fun OfficeCount(label: String, count: Int, modifier: Modifier) {
    Card(modifier, colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFDEE3E9))) {
        Column(Modifier.padding(12.dp)) {
            Text("$count", color = navy, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(label, color = muted, fontSize = 11.sp)
        }
    }
}

@Composable
internal fun AdminOverview(onRules: () -> Unit, onAudit: () -> Unit) {
    Text("Technical administration", color = navy, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = navy),
        shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Safe by design.", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Manage access and policy without opening private ownership evidence.",
                color = Color(0xFFCDDAEA), fontSize = 13.sp)
        }
    }
    OutlinedButton(onClick = onRules, modifier = Modifier.fillMaxWidth()) { Text("Category rules") }
    OutlinedButton(onClick = onAudit, modifier = Modifier.fillMaxWidth()) { Text("Access & audit") }
}

@Composable
internal fun AdminRules(rules: List<JSONObject>, online: Boolean, onSave: (String, Int, Boolean, Boolean) -> Unit) {
    Text("Category rules", color = navy, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    ConnectedNotice("Policy configuration", "Retention and restricted custody rules are stored by the service. Wallets and documents always require sealing.")
    if (!online) ConnectedNotice("Rules unavailable", "Connect to the admin service to view or save category rules.")
    rules.forEach { rule ->
        val id = rule.optString("category_id")
        var days by remember(id) { mutableStateOf(if (rule.has("retention_days")) rule.optInt("retention_days").toString() else "") }
        var restricted by remember(id) { mutableStateOf(rule.optBoolean("restricted", id in listOf("wallet", "documents"))) }
        var sealed by remember(id) { mutableStateOf(rule.optBoolean("requires_seal", id in listOf("wallet", "documents"))) }
        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFDEE3E9))) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(id.replace('_', ' ').replaceFirstChar { it.uppercase() }, color = navy,
                    fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                if (!rule.has("retention_days")) Text("No saved rule yet", color = muted, fontSize = 12.sp)
                OutlinedTextField(days, { days = it.filter(Char::isDigit).take(3) },
                    label = { Text("Retention days") }, modifier = Modifier.fillMaxWidth())
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(restricted, { restricted = it }, enabled = id !in listOf("wallet", "documents"))
                    Text("Restricted custody", color = navy, fontSize = 13.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(sealed, { sealed = it }, enabled = id !in listOf("wallet", "documents"))
                    Text("Seal on receipt", color = navy, fontSize = 13.sp)
                }
                Button(enabled = days.toIntOrNull()?.let { it in 1..365 } == true, onClick = {
                    onSave(id, days.toInt(), restricted, sealed)
                }, colors = ButtonDefaults.buttonColors(containerColor = blue)) { Text("Save rule") }
            }
        }
    }
}

@Composable
internal fun AdminAudit(events: List<JSONObject>, online: Boolean, onExport: () -> Unit) {
    Text("Access & audit", color = navy, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    ConnectedNotice("Access boundary", "Students see their own reports and claims. Office staff review custody and ownership. Technical admins do not receive private claim evidence.")
    if (!online) ConnectedNotice("Audit unavailable", "Connect to the admin service to load recent events.")
    else if (events.isEmpty()) ConnectedNotice("No audit events", "Recorded claim decisions and policy changes will appear here.")
    events.forEach { event -> ConnectedNotice(event.optString("action").replace('_', ' '),
        listOf(event.optString("category_id"), event.optString("actor_uid"), event.optString("at"))
            .filter { it.isNotBlank() }.joinToString(" · ")) }
    if (online && events.isNotEmpty()) {
        ConnectedNotice("Local export", "Save a CSV to this device's Downloads folder. It contains action, category and time only.")
        OutlinedButton(enabled = android.os.Build.VERSION.SDK_INT >= 29, onClick = onExport,
            modifier = Modifier.fillMaxWidth()) { Text("Save redacted CSV") }
        if (android.os.Build.VERSION.SDK_INT < 29)
            Text("Downloads export requires Android 10 or newer.", color = muted, fontSize = 12.sp)
    }
}
