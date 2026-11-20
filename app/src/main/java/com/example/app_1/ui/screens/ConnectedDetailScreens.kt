package com.example.app_1.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONObject

private val ink = Color(0xFF112B4B)
private val actionBlue = Color(0xFF245ECA)

@Composable
internal fun ConnectedCandidateScreen(candidate: JSONObject?, busy: Boolean, onBack: () -> Unit,
    onClaim: (String) -> Unit) {
    var proof by remember(candidate?.optString("id")) { mutableStateOf("") }
    Text("Candidate details", color = ink, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    if (candidate == null) {
        ConnectedNotice("Candidate unavailable", "The office record could not be loaded. Please try again later.")
        OutlinedButton(onClick = onBack) { Text("Back to matches") }
        return
    }
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFDEE3E9))) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Held by office", color = Color(0xFF28775C), fontSize = 11.sp)
            Text(candidate.optString("item_name"), color = ink, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("${candidate.optString("category_id").replace('_', ' ')} · ${candidate.optString("location_id").replace('_', ' ')}",
                color = Color(0xFF697586), fontSize = 12.sp)
            Text("General details only · unique identifiers hidden", color = Color(0xFF697586), fontSize = 11.sp)
        }
    }
    Text("Is this yours?", color = ink, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    Text("Tell office staff a detail only the owner would know. Do not enter ID numbers or passwords.",
        color = Color(0xFF697586), fontSize = 13.sp)
    OutlinedTextField(proof, { proof = it.take(2000) }, modifier = Modifier.fillMaxWidth(),
        label = { Text("Private ownership detail") }, minLines = 3)
    ConnectedNotice("Private review", "Your answer is available to the Lost & Found Office for in-person verification. A match score does not prove ownership.")
    Button(enabled = !busy && proof.trim().length >= 10, onClick = { onClaim(proof.trim()) },
        modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = actionBlue)) {
        Text("Request ownership review")
    }
    TextButton(onClick = onBack) { Text("Not mine") }
}

@Composable
internal fun ConnectedVerificationScreen(claim: JSONObject, busy: Boolean, onBack: () -> Unit,
    onDecision: (String) -> Unit) {
    Text("Ownership verification", color = ink, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    ConnectedNotice("Claim ${claim.optString("id").take(12)}", "Status: ${claim.optString("status")}")
    ConnectedNotice("Private owner response", claim.optString("ownership_details", "No private answer was recorded for this older claim."))
    ConnectedNotice("Office check", "Compare the response with the item in custody and verify the claimant in person. Do not approve from the match score alone.")
    if (claim.optString("status") == "pending") {
        Button(enabled = !busy, onClick = { onDecision("approved") }, modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = actionBlue)) { Text("Approve for pickup") }
        OutlinedButton(enabled = !busy, onClick = { onDecision("rejected") }, modifier = Modifier.fillMaxWidth()) {
            Text("Reject claim")
        }
    }
    TextButton(onClick = onBack) { Text("Keep on hold") }
}

@Composable
internal fun ConnectedHandoverScreen(claim: JSONObject, receipt: JSONObject?, busy: Boolean,
    onBack: () -> Unit, onCollect: () -> Unit) {
    val status = claim.optString("status")
    Text(if (status == "rejected") "Claim decision" else "Pickup & recorded handover",
        color = ink, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    ConnectedNotice(when (status) {
        "rejected" -> "Claim rejected"
        "collected" -> "Handover recorded"
        else -> "Approved claim"
    }, when (status) {
        "rejected" -> "This claim was rejected after office review. The item remains in custody."
        "collected" -> "This item was handed over and the collection was recorded."
        else -> "Claim ${claim.optString("id").take(12)} is ready for pickup. Verify the person and item at the office before completing the handover."
    })
    if (status == "approved") {
        Button(enabled = !busy, onClick = onCollect, modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = actionBlue)) { Text("Record physical handover") }
    }
    if (receipt != null) {
        ConnectedNotice("Recorded handover receipt", "${receipt.optString("item_name")} · ${receipt.optString("collected_at")}")
        Text("Reference: ${receipt.optString("claim_id")}", color = ink, fontSize = 12.sp)
    } else if (claim.optString("status") == "collected") {
        ConnectedNotice("Receipt unavailable", "The collection was recorded, but the receipt could not be loaded. Try again when the service is online.")
    }
    TextButton(onClick = onBack) { Text("Back to claims") }
}

@Composable
internal fun ConnectedClaimStatusScreen(claim: JSONObject, receipt: JSONObject?, onBack: () -> Unit) {
    Text("Claim status", color = ink, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    val status = claim.optString("status")
    ConnectedNotice("Ownership request · $status", when (status) {
        "pending" -> "The office is reviewing your request. Bring private proof of ownership in person."
        "approved" -> "Your request was approved. Arrange pickup with the Lost & Found Office."
        "collected" -> "The office recorded physical handover of this item."
        "rejected" -> "This claim could not be verified. Contact the office for help."
        else -> "Ask the office for the latest status."
    })
    ConnectedNotice("What happens next", "Office staff make the final ownership decision. A suggested match alone does not establish ownership.")
    if (status == "collected") {
        if (receipt != null) ConnectedNotice("Handover receipt", "${receipt.optString("item_name")} · ${receipt.optString("collected_at")}\nReference: ${receipt.optString("claim_id")}")
        else ConnectedNotice("Receipt unavailable", "Reconnect and open this status again to load the receipt.")
    }
    TextButton(onClick = onBack) { Text("Back to my reports") }
}
