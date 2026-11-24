package com.example.app_1.ui.screens

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_1.BuildConfig
import com.example.app_1.R
import com.example.app_1.data.ApiException
import com.example.app_1.data.FirebaseRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import java.net.URLEncoder
import java.util.Locale

private fun JSONObject.rows(key: String): List<JSONObject> {
    val array = optJSONArray(key) ?: JSONArray()
    return (0 until array.length()).map { array.getJSONObject(it) }
}

private fun savedReportDrafts(preferences: android.content.SharedPreferences): List<Pair<String, JSONObject>> =
    preferences.all.mapNotNull { (key, value) ->
        if (key != "draft" && !key.startsWith("draft_")) return@mapNotNull null
        runCatching { key to JSONObject(value as String) }.getOrNull()
    }.sortedByDescending { it.second.optLong("updated_at") }


/** Connected workflow. The old sample screens remain available in source, not as authorization. */
@Composable
fun ConnectedApp(incomingLink: String? = null, onLinkConsumed: () -> Unit = {}) {
    val context = LocalContext.current
    val configured = BuildConfig.FIREBASE_PROJECT_ID.isNotBlank() && BuildConfig.FIREBASE_APP_ID.isNotBlank() &&
        BuildConfig.FIREBASE_API_KEY.isNotBlank()
    if (!configured) {
        Column(Modifier.fillMaxSize().safeDrawingPadding().padding(24.dp)) {
            Text("Lost & Found", style = MaterialTheme.typography.headlineMedium)
            Text("Firebase is not configured for this build. Follow FIREBASE_SETUP.md to connect your project.")
        }
        return
    }
    val repository = remember { (context.applicationContext as com.example.app_1.LostFoundApplication).repository!! }
    val scope = rememberCoroutineScope()
    var identity by remember { mutableStateOf<JSONObject?>(null) }
    var email by rememberSaveable { mutableStateOf("") }
    var demoPassword by rememberSaveable { mutableStateOf("") }
    var pastedLink by rememberSaveable { mutableStateOf("") }
    var showPasteLink by rememberSaveable { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var messageIsSuccess by remember { mutableStateOf(false) }
    val signInScroll = rememberScrollState()
    var page by rememberSaveable { mutableStateOf("Home") }
    var backendOnline by remember { mutableStateOf(false) }
    var reports by remember { mutableStateOf(emptyList<JSONObject>()) }
    var matches by remember { mutableStateOf(emptyList<JSONObject>()) }
    var matchingStatus by remember { mutableStateOf("") }
    var matchesLoaded by remember { mutableStateOf(false) }
    var claims by remember { mutableStateOf(emptyList<JSONObject>()) }
    var notifications by remember { mutableStateOf(emptyList<JSONObject>()) }
    var categoryRules by remember { mutableStateOf(emptyList<JSONObject>()) }
    var auditEvents by remember { mutableStateOf(emptyList<JSONObject>()) }
    var adminAccount by remember { mutableStateOf<JSONObject?>(null) }
    var adminOnline by remember { mutableStateOf(false) }
    var adminRefreshKey by remember { mutableIntStateOf(0) }
    var unclaimedItems by remember { mutableStateOf(emptyList<JSONObject>()) }
    var retentionPolicyCount by remember { mutableIntStateOf(0) }
    var unclaimedOnline by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf<JSONObject?>(null) }
    var selectedMatch by remember { mutableStateOf<JSONObject?>(null) }
    var candidate by remember { mutableStateOf<JSONObject?>(null) }
    var selectedClaim by remember { mutableStateOf<JSONObject?>(null) }
    var receipt by remember { mutableStateOf<JSONObject?>(null) }
    var reportsTab by rememberSaveable { mutableStateOf("Active") }
    var draftToOpen by rememberSaveable { mutableStateOf<String?>(null) }
    var reportFormNonce by rememberSaveable { mutableIntStateOf(0) }
    var pendingDecision by remember { mutableStateOf<Pair<String, String>?>(null) }
    var photoBytes by remember { mutableStateOf<ByteArray?>(null) }
    val notificationPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    LaunchedEffect(identity?.optString("uid")) {
        if (identity != null && !BuildConfig.USE_FIREBASE_EMULATORS) {
            if (android.os.Build.VERSION.SDK_INT >= 33) notificationPermission.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            runCatching { repository.registerPush() }
        }
    }
    fun run(action: suspend () -> Unit) {
        if (busy) return
        scope.launch {
            busy = true
            message = ""
            messageIsSuccess = false
            try { action() } catch (e: Exception) {
                message = if (identity == null && repository.auth.currentUser?.isEmailVerified == true) {
                    messageIsSuccess = true
                    "Signed in. Reports are temporarily unavailable."
                } else if (identity == null) signInError(e) else {
                    if (e !is ApiException) backendOnline = false
                    messageIsSuccess = false
                    when (e) {
                        is ApiException -> when (e.statusCode) {
                            401 -> "Your sign-in needs refreshing. Sign out, sign in again, and retry."
                            403 -> "Access restricted: ${e.userMessage}"
                            else -> e.userMessage
                        }
                        else -> if (page == "New" || page == "NewFound")
                            "The service is unavailable. Your draft is saved on this device; retry when connected."
                        else "The service is unavailable right now. Check your connection and retry."
                    }
                }
            }
            finally { busy = false }
        }
    }
    suspend fun refresh() {
        reports = repository.request("GET", "/v1/reports").rows("reports")
        claims = repository.request("GET", "/v1/claims").rows("claims")
        notifications = repository.request("GET", "/v1/notifications").rows("notifications")
        backendOnline = true
    }
    suspend fun loadMatches(row: JSONObject) {
        selected = row
        matchesLoaded = false
        matches = emptyList()
        matchingStatus = ""
        val response = repository.request("GET", "/v1/reports/${row.getString("report_type")}/${row.getString("id")}/matches")
        matches = response.rows("matches")
        matchingStatus = response.optString("matching_status")
        matchesLoaded = true
    }
    LaunchedEffect(page, identity?.optString("role"), adminRefreshKey) {
        if (identity?.optString("role") == "admin" && page in setOf("Rules", "Audit")) {
            runCatching {
                if (page == "Rules") categoryRules = repository.request("GET", "/v1/admin/category-rules").rows("rules")
                if (page == "Audit") auditEvents = repository.request("GET", "/v1/admin/audit").rows("events")
                adminOnline = true
            }.onFailure { adminOnline = false }
        }
        if (identity?.optString("role") == "office_staff" && page == "Unclaimed") {
            runCatching {
                val response = repository.request("GET", "/v1/office/unclaimed")
                unclaimedItems = response.rows("items")
                retentionPolicyCount = response.optInt("policy_configured_categories")
                unclaimedOnline = true
            }.onFailure { unclaimedOnline = false }
        }
    }
    LaunchedEffect(Unit) {
        if (repository.auth.currentUser != null) run {
            if (repository.auth.currentUser?.isEmailVerified == true || repository.checkEmailVerified()) {
                identity = repository.localIdentity()
                page = when (identity?.optString("role")) { "office_staff" -> "Office"; "admin" -> "Admin"; else -> "Home" }
                runCatching { refresh() }.onFailure { backendOnline = false }
            }
        }
    }
    LaunchedEffect(message) {
        if (identity == null && message.isNotBlank()) signInScroll.animateScrollTo(signInScroll.maxValue)
    }
    LaunchedEffect(incomingLink, email) {
        if (!incomingLink.isNullOrBlank() &&
            (repository.hasPendingEmailLink() || email.trim().endsWith("@strathmore.edu", ignoreCase = true))) run {
            if (!repository.isEmailSignInLink(incomingLink)) {
                message = "This link has expired. Request a new one."
                onLinkConsumed()
            } else {
                repository.completeEmailLink(incomingLink, email)
                identity = repository.localIdentity()
                page = when (identity?.optString("role")) { "office_staff" -> "Office"; "admin" -> "Admin"; else -> "Home" }
                onLinkConsumed()
                runCatching { refresh() }.onFailure { backendOnline = false }
            }
        }
    }
    if (identity == null) {
        Column(Modifier.fillMaxSize().background(Color.White).safeDrawingPadding().imePadding().verticalScroll(signInScroll)) {
            Box(Modifier.fillMaxWidth().height(376.dp)) {
                Image(painterResource(R.drawable.signin_figma_background), null,
                    Modifier.fillMaxSize(), contentScale = ContentScale.Crop,
                    alignment = Alignment.TopCenter)
                Text("Sign in", Modifier.align(Alignment.CenterStart).padding(start = 20.dp),
                    color = Color(0xFF102B49), fontSize = 40.sp, fontWeight = FontWeight.Bold)
            }
            Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)) {
                if (repository.auth.currentUser?.isEmailVerified == true) {
                    Text("You're signed in.", color = Color(0xFF102B49),
                        fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
                    Text("Your reports are temporarily unavailable. Try again in a moment.",
                        color = Color(0xFF102B49), fontSize = 16.sp, lineHeight = 24.sp)
                    Button(enabled = !busy, modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF245ECA)),
                        onClick = { run {
                            identity = repository.localIdentity()
                            page = when (identity?.optString("role")) { "office_staff" -> "Office"; "admin" -> "Admin"; else -> "Home" }
                            runCatching { refresh() }.onFailure { backendOnline = false }
                        } }) {
                        Text("Try again")
                    }
                    TextButton(onClick = {
                        repository.auth.signOut()
                        message = ""
                        email = ""
                    }) { Text("Sign out") }
                    if (busy) LinearProgressIndicator(Modifier.fillMaxWidth())
                    if (message.isNotBlank()) Text(message,
                        Modifier.fillMaxWidth().background(Color(0xFFE8F5EC), RoundedCornerShape(10.dp)).padding(14.dp),
                        color = Color(0xFF17613A), fontSize = 14.sp)
                    return@Column
                }
                Text("Use your Strathmore University email. We’ll send a one-time sign-in link to your inbox.",
                    color = Color(0xFF102B49), fontSize = 16.sp, lineHeight = 24.sp)
                Spacer(Modifier.height(6.dp))
                Text("University email", color = Color(0xFF102B49), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(email, { email = it }, placeholder = { Text("name@strathmore.edu") },
                    singleLine = true, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth())
                if (BuildConfig.USE_FIREBASE_EMULATORS) {
                    ConnectedNotice("Local demonstration", "Use a fictional account seeded in the Firebase emulator. No live reports are used.")
                    OutlinedTextField(demoPassword, { demoPassword = it }, label = { Text("Demo password") },
                        visualTransformation = PasswordVisualTransformation(), singleLine = true,
                        modifier = Modifier.fillMaxWidth())
                    Button(enabled = !busy && email.trim().endsWith("@strathmore.edu", ignoreCase = true) && demoPassword.isNotBlank(),
                        onClick = { run {
                            repository.signIn(email, demoPassword)
                            identity = repository.localIdentity()
                            page = when (identity?.optString("role")) { "office_staff" -> "Office"; "admin" -> "Admin"; else -> "Home" }
                            refresh()
                        } }, modifier = Modifier.fillMaxWidth()) { Text("Sign in to local demo") }
                }
                Row(Modifier.fillMaxWidth().background(Color(0xFFEDF3FE), RoundedCornerShape(10.dp)).padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(painterResource(R.drawable.signin_shield_check), null, tint = Color.Unspecified)
                    Text("Only @strathmore.edu addresses can receive a sign-in link.",
                        color = Color(0xFF102B49), fontSize = 13.sp, lineHeight = 19.sp)
                }
                if (!incomingLink.isNullOrBlank()) {
                    Text("Use the email address that received this link. You can correct it here if this phone sent a link for someone else.",
                        color = Color(0xFF102B49), fontSize = 14.sp)
                }
                Button(enabled = !busy && email.trim().endsWith("@strathmore.edu", ignoreCase = true),
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF245ECA)),
                    onClick = { run {
                        repository.sendEmailLink(email)
                        messageIsSuccess = true
                        message = "Link sent! Check your inbox."
                    } }) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("Email me a sign-in link", fontSize = 14.sp)
                        Icon(painterResource(R.drawable.signin_arrow_right), null, tint = Color.Unspecified)
                    }
                }
                TextButton(onClick = { showPasteLink = !showPasteLink }) {
                    Text(if (showPasteLink) "Hide link entry" else "Link opened in a browser? Paste it here")
                }
                if (showPasteLink) {
                    Text("Copy the full sign-in link from your email. Enter the same university email above, then paste the link below.",
                        color = Color(0xFF697586), fontSize = 13.sp, lineHeight = 19.sp)
                    OutlinedTextField(pastedLink, { pastedLink = it.trim() },
                        label = { Text("Full sign-in link") }, minLines = 2,
                        shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth())
                    Button(enabled = !busy && email.trim().endsWith("@strathmore.edu", ignoreCase = true)
                        && pastedLink.startsWith("https://"),
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF245ECA)),
                        onClick = { run {
                            repository.completeEmailLink(pastedLink, email)
                            identity = repository.localIdentity()
                            page = when (identity?.optString("role")) { "office_staff" -> "Office"; "admin" -> "Admin"; else -> "Home" }
                            pastedLink = ""
                            runCatching { refresh() }.onFailure { backendOnline = false }
                        } }) { Text("Finish sign-in") }
                }
                if (busy) LinearProgressIndicator(Modifier.fillMaxWidth())
                if (message.isNotBlank()) {
                    Text(message,
                        Modifier.fillMaxWidth().background(
                            if (messageIsSuccess) Color(0xFFE8F5EC) else Color(0xFFFFF1EF),
                            RoundedCornerShape(10.dp)).padding(14.dp),
                        color = if (messageIsSuccess) Color(0xFF17613A) else Color(0xFF9B2C24),
                        fontSize = 14.sp, lineHeight = 20.sp)
                }
            }
        }
        return
    }
    val role = identity!!.optString("role", "student")
    Column(Modifier.fillMaxSize().background(Color(0xFFF8F7F2)).safeDrawingPadding()) {
        if (BuildConfig.ML_API_BASE_URL.isBlank()) {
            ConnectedNotice("Service not configured", "Firebase sign-in is available, but reports and matches need ML_API_BASE_URL in firebase.properties.")
        }
        if (page == "Home") {
            ConnectedHomeScreen(
                email = repository.auth.currentUser?.email.orEmpty(), role = role,
                reports = reports, backendOnline = backendOnline,
                onLost = { draftToOpen = null; reportFormNonce++; page = "New" },
                onFound = { draftToOpen = null; reportFormNonce++; page = if (role == "student") "FoundHelp" else "NewFound" },
                onReports = { page = "Reports" }, onMatches = { page = "Matches" },
                onProfile = { page = "Profile" }, modifier = Modifier.weight(1f))
        } else {
    ConnectedPageHeader(when (page) { "Reports" -> "My reports"; "Matches" -> "Possible matches"; "Candidate" -> "Candidate details"; "ClaimStatus" -> "Claim status"; "Verification" -> "Ownership verification"; "Handover" -> "Pickup & handover"; "Alerts" -> "Notifications"; "Inventory" -> "Inventory"; "Claims" -> "Claims queue"; "Unclaimed" -> "Unclaimed items"; "Rules" -> "Category rules"; "Audit" -> "Access & audit"; else -> "Lost & Found" },
        when (role) { "office_staff" -> "LOST & FOUND OFFICE"; "admin" -> "TECHNICAL ADMINISTRATION"; else -> "STRATHMORE UNIVERSITY" })
    Column(Modifier.weight(1f).imePadding().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (BuildConfig.USE_FIREBASE_EMULATORS) Text("Local emulator · fictional test accounts only")
        if (busy) LinearProgressIndicator(Modifier.fillMaxWidth())
        if (message.isNotBlank()) Text(message, style = MaterialTheme.typography.bodyMedium)
        if (identity != null) {
            val staff = role == "office_staff"
            if (page in setOf("Reports", "Matches", "Alerts", "Claims", "Inventory", "Office"))
                TextButton(enabled = !busy, onClick = { run { refresh() } }) { Text("Refresh updates") }
            if (role == "admin" && page in setOf("Rules", "Audit"))
                TextButton(enabled = !busy, onClick = { adminRefreshKey++ }) { Text("Refresh admin data") }
            when (page) {
                "Office" -> OfficeDashboard(reports, claims, backendOnline,
                    onCheckIn = { draftToOpen = null; reportFormNonce++; page = "NewFound" }, onInventory = { page = "Inventory" },
                    onClaims = { page = "Claims" }, onUnclaimed = { page = "Unclaimed" })
                "Unclaimed" -> OfficeUnclaimed(unclaimedItems, unclaimedOnline, retentionPolicyCount)
                "Admin" -> AdminOverview(onRules = { page = "Rules" }, onAudit = { page = "Audit" })
                "Rules" -> AdminRules(categoryRules, adminOnline) { id, days, restricted, sealed ->
                    run {
                        repository.request("PUT", "/v1/admin/category-rules/$id", JSONObject()
                            .put("retention_days", days).put("restricted", restricted).put("requires_seal", sealed))
                        categoryRules = repository.request("GET", "/v1/admin/category-rules").rows("rules")
                        messageIsSuccess = true
                        message = "Rule saved."
                    }
                }
                "Audit" -> AdminAudit(auditEvents, adminOnline, adminAccount, identity!!.optString("uid"), busy,
                    onFind = { address -> run {
                        adminAccount = null
                        val encoded = URLEncoder.encode(address.trim().lowercase(), "UTF-8")
                        try {
                            adminAccount = repository.request("GET", "/v1/admin/accounts?email=$encoded").getJSONObject("account")
                        } catch (error: Exception) {
                            message = if (error.message?.contains("Account not found") == true)
                                "No account yet. This person must sign in once before you can assign a role."
                            else "Could not find the account. Check your connection and try again."
                        }
                    } },
                    onRole = { uid, role -> run {
                        adminAccount = repository.request("PUT", "/v1/admin/accounts/$uid/role",
                            JSONObject().put("role", role)).getJSONObject("account")
                        runCatching { auditEvents = repository.request("GET", "/v1/admin/audit").rows("events") }
                        messageIsSuccess = true
                        message = "Role saved. Ask the person to sign in again."
                    } },
                    onExport = {
                    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
                        messageIsSuccess = false
                        message = "Downloads export requires Android 10 or newer."
                    } else {
                    val rows = auditEvents.map { event ->
                        RedactedAuditRow(event.optString("action"), event.optString("category_id"), event.optString("at"))
                    }
                    scope.launch {
                        runCatching {
                            withContext(Dispatchers.IO) {
                                saveAuditCsvToDownloads(context, redactedAuditCsv(rows))
                            }
                        }.onSuccess { name ->
                            messageIsSuccess = true
                            message = "Saved $name to Downloads/LostAndFound."
                        }.onFailure {
                            messageIsSuccess = false
                            message = "Could not save the CSV. Please try again."
                        }
                    }
                    }
                })
                "Inventory" -> OfficeInventory(reports, backendOnline) {
                    draftToOpen = null; reportFormNonce++; page = "NewFound"
                }
                "Candidate" -> ConnectedCandidateScreen(candidate, busy,
                    onBack = { page = "Reports" }, onClaim = { proof ->
                        val matchId = selectedMatch?.optString("id").orEmpty()
                        if (!matchId.matches(Regex("^[A-Za-z0-9_-]{1,160}$"))) {
                            message = "This suggestion is no longer available. Refresh matches and select it again."
                            messageIsSuccess = false
                        } else run {
                            repository.request("POST", "/v1/matches/$matchId/claim", JSONObject().put("ownership_details", proof))
                            messageIsSuccess = true
                            message = "Ownership request sent to the office."
                            page = "Reports"; selectedMatch = null; candidate = null
                            runCatching { refresh() }.onFailure {
                                backendOnline = false
                                message = "Ownership request sent. Reports could not refresh; try Refresh updates."
                            }
                        }
                    })
                "ClaimStatus" -> selectedClaim?.let { claim -> ConnectedClaimStatusScreen(claim, receipt) {
                    page = "Reports"
                } }
                "Verification" -> selectedClaim?.let { claim -> ConnectedVerificationScreen(claim, busy,
                    onBack = { page = "Claims" }, onDecision = { action -> pendingDecision = claim.optString("id") to action }) }
                "Handover" -> selectedClaim?.let { claim -> ConnectedHandoverScreen(claim, receipt, busy,
                    onBack = { page = "Claims" }, onCollect = { pendingDecision = claim.optString("id") to "collected" }) }
                "Profile" -> {
                    Text("Your account", color = Color(0xFF112B4B), fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    ConnectedNotice(repository.auth.currentUser?.email.orEmpty(),
                        "Role: ${when (role) { "admin" -> "Administrator"; "office_staff" -> "Office staff"; else -> "Student" }}")
                    ConnectedNotice("Account access", "Office and admin roles are assigned through verified account claims.")
                    OutlinedButton(onClick = {
                        repository.auth.signOut(); identity = null; page = "Home"
                        reports = emptyList(); matches = emptyList(); claims = emptyList(); notifications = emptyList()
                        selected = null; selectedMatch = null; candidate = null; selectedClaim = null; receipt = null
                    }) { Text("Sign out") }
                }
                "New", "NewFound" -> key(identity!!.getString("uid"), page, draftToOpen, reportFormNonce) {
                    ConnectedReportForm(repository, staff, busy, { action -> run(action) },
                        initialFound = page == "NewFound", draftKey = draftToOpen,
                        onStartAnother = {
                            draftToOpen = null; reportFormNonce++
                        }, onSaved = { reportId ->
                        message = "Report saved: $reportId. Matching will run separately."
                        messageIsSuccess = true
                        page = if (staff) "Inventory" else "Reports"
                        runCatching { refresh() }.onFailure {
                            backendOnline = false
                            message = "Report saved: $reportId. Reports could not refresh; try Refresh updates."
                        }
                    })
                }
                "FoundHelp" -> {
                    Text("I found something", color = Color(0xFF112B4B), fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    ConnectedNotice("Bring it to the office", "Please bring the item to the Lost & Found Office in the Student Centre. Staff will record custody and arrange a safe handover.")
                    Button(onClick = { page = "Home" }) { Text("Back to Home") }
                }
                "Matches" -> {
                    ConnectedNotice("How matching works", "Suggestions narrow the search. Office staff verify ownership before handover.")
                    if (!backendOnline) ConnectedNotice("Matches unavailable", "Connect to the service to check for suggestions.")
                    else if (reports.none { it.optString("report_type") == "lost" }) ConnectedNotice("No lost reports yet", "Report something you lost to start looking for possible matches.")
                    else reports.filter { it.optString("report_type") == "lost" }.forEach { row ->
                        Card(onClick = { selected = row; page = "Reports"; run {
                            loadMatches(row)
                        } }, modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp)) {
                                Text(row.optString("item_name"), color = Color(0xFF112B4B), fontWeight = FontWeight.SemiBold)
                                Text("Tap to review suggestions", color = Color(0xFF697586), fontSize = 12.sp)
                            }
                        }
                    }
                }
                "Reports" -> {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Active", "Resolved", "Unfinished").forEach { tab ->
                            FilterChip(selected = reportsTab == tab, onClick = { reportsTab = tab }, label = { Text(tab) })
                        }
                    }
                    if (!backendOnline) ConnectedNotice("Reports unavailable", "Your saved draft remains on this device. Try again when the service is online.")
                    val visibleReports = reports.filter { row ->
                        when (reportsTab) { "Active" -> row.optString("status") == "active"; "Resolved" -> row.optString("status") != "active"; else -> false }
                    }
                    if (backendOnline && visibleReports.isEmpty() && reportsTab != "Unfinished")
                        ConnectedNotice(if (reportsTab == "Active") "No active reports" else "No resolved reports", "Submitted reports will appear here.")
                    if (reportsTab == "Unfinished") {
                        val draft = context.getSharedPreferences("connected_draft_${identity!!.getString("uid")}", android.content.Context.MODE_PRIVATE)
                        val entries = savedReportDrafts(draft)
                        if (entries.isEmpty()) ConnectedNotice("No unfinished reports", "Reports you start and save on this device will appear here.")
                        entries.forEach { (draftKey, details) ->
                            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(details.optString("title").ifBlank { "Untitled report" },
                                        color = Color(0xFF112B4B), fontWeight = FontWeight.SemiBold)
                                    Text(if (details.optBoolean("attempted")) "Submission not confirmed · review or retry"
                                         else "Draft saved on this device", color = Color(0xFF697586), fontSize = 12.sp)
                                    TextButton(onClick = {
                                        draftToOpen = draftKey; reportFormNonce++
                                        page = if (details.optBoolean("found")) "NewFound" else "New"
                                    }) { Text("Open draft") }
                                }
                            }
                        }
                    }
                    Button(onClick = { draftToOpen = null; reportFormNonce++; page = "New" }) {
                        Text("Report a lost item")
                    }
                    if (reportsTab != "Unfinished") visibleReports.forEach { row ->
                        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)) {
                            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(row.optString("item_name"), style = MaterialTheme.typography.titleMedium)
                                Text("${row.optString("report_type")} Â· ${row.optString("status")} Â· matching ${row.optString("matching_status")}")
                                Text(row.optString("description"))
                                TextButton(enabled = !busy, onClick = { run {
                                    photoBytes = null
                                    loadMatches(row)
                                } }) { Text("Review suggestions") }
                                if (row.optString("photo_reference").isNotBlank()) TextButton(enabled = !busy, onClick = { run {
                                    selected = row; photoBytes = repository.photo(row.getString("photo_reference"))
                                } }) { Text("View private photo") }
                                if (row.optString("matching_status") in listOf("failed", "complete")) TextButton(enabled = !busy, onClick = { run {
                                    repository.request("POST", "/v1/reports/${row.getString("report_type")}/${row.getString("id")}/retry", JSONObject())
                                    message = "Matching queued again."; refresh()
                                } }) { Text("Run matching again") }
                            }
                        }
                    }
                    if (backendOnline && reportsTab != "Unfinished") {
                        val relevant = claims.filter { claim -> reports.any { it.optString("id") == claim.optString("lost_id") } }
                        if (relevant.isNotEmpty()) Text("Claim status", color = Color(0xFF112B4B), fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        relevant.forEach { claim -> Card(onClick = {
                            selectedClaim = claim; receipt = null; page = "ClaimStatus"
                            if (claim.optString("status") == "collected") run {
                                receipt = repository.request("GET", "/v1/claims/${claim.getString("id")}/receipt")
                            }
                        }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)) {
                            Column(Modifier.padding(16.dp)) {
                                Text("Ownership request · ${claim.optString("status")}", color = Color(0xFF112B4B), fontWeight = FontWeight.SemiBold)
                                Text("View status and next steps", color = Color(0xFF697586), fontSize = 12.sp)
                            }
                        } }
                    }
                    if (selected != null) {
                        Text("Selected: ${selected!!.optString("item_name")}", style = MaterialTheme.typography.titleMedium)
                        photoBytes?.let { bytes ->
                            val bitmap = remember(bytes) { BitmapFactory.decodeByteArray(bytes, 0, bytes.size) }
                            if (bitmap != null) Image(bitmap.asImageBitmap(), "Report photograph", Modifier.fillMaxWidth().heightIn(max = 280.dp))
                        }
                        Text("Model scores rank suggestions; they do not establish ownership. Office staff verify every claim.")
                        if (!matchesLoaded) ConnectedNotice("Suggestions not loaded", "Tap Review suggestions to load the current matching result.")
                        else if (matchingStatus in listOf("pending", "running"))
                            ConnectedNotice("Matching in progress", "The office inventory is being checked. Refresh suggestions shortly.")
                        else if (matchingStatus == "failed")
                            ConnectedNotice("Matching could not finish", "Use Run matching again, then refresh suggestions.")
                        else if (matches.isEmpty())
                            ConnectedNotice("No text candidates", "No item in the current office inventory shares enough text to be suggested. Staff can search manually; this does not prove the item is absent.")
                        if (matchesLoaded && matchingStatus in listOf("pending", "running", "complete"))
                            TextButton(enabled = !busy, onClick = { run { selected?.let { loadMatches(it) } } }) { Text("Refresh suggestions") }
                        matches.forEachIndexed { index, match ->
                            Card(onClick = { if (match.optString("owner_uid") == identity!!.getString("uid")) run {
                                selectedMatch = match; candidate = null
                                candidate = repository.request("GET", "/v1/matches/${match.getString("id")}/candidate").getJSONObject("candidate")
                                page = "Candidate"
                            } }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)) {
                                Column(Modifier.padding(16.dp)) {
                                    Text("#${index + 1} ${match.optString("item_name").ifBlank { "Possible office match" }}",
                                        color = Color(0xFF112B4B), fontWeight = FontWeight.SemiBold)
                                    val similarity = match.optDouble("score", Double.NaN)
                                    if (similarity.isFinite()) Text("TF-IDF text similarity: ${String.format(Locale.US, "%.3f", similarity)}",
                                        color = Color(0xFF697586), fontSize = 12.sp)
                                    Text(match.optString("explanation").ifBlank { "Staff review is required before any ownership decision." },
                                        color = Color(0xFF697586), fontSize = 12.sp)
                                    Text("Review general details and provide private proof", color = Color(0xFF697586), fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
                "Claims" -> OfficeClaimsQueue(claims, reports, backendOnline, busy) { claim ->
                    selectedClaim = claim; receipt = null
                    page = if (claim.optString("status") == "pending") "Verification" else "Handover"
                    if (claim.optString("status") == "collected") run {
                        receipt = repository.request("GET", "/v1/claims/${claim.getString("id")}/receipt")
                    }
                }
                "Alerts" -> {
                    if (!backendOnline) ConnectedNotice("Notifications unavailable", "Connect to the service to check for updates.")
                    else if (notifications.isEmpty()) ConnectedNotice("All caught up", "Updates about reports and claims will appear here.")
                    notifications.forEach { ConnectedNotice("Update", it.optString("message")) }
                }
            }
        }
    }
        }
        pendingDecision?.let { (claimId, action) ->
            AlertDialog(onDismissRequest = { pendingDecision = null },
                title = { Text(if (action == "collected") "Confirm physical handover" else "Confirm claim decision") },
                text = { Text(when (action) {
                    "approved" -> "Approve only after checking private ownership evidence in person."
                    "collected" -> "Mark collected only after the item has been handed to the verified owner."
                    else -> "Reject this claim after reviewing the available evidence."
                }) },
                confirmButton = { TextButton(onClick = {
                    pendingDecision = null
                    run {
                        repository.request("POST", "/v1/claims/$claimId/decision", JSONObject().put("status", action))
                        message = "Review saved."; messageIsSuccess = true
                        selectedClaim = claims.firstOrNull { it.optString("id") == claimId }
                        if (action == "approved") page = "Handover"
                        if (action == "collected") {
                            page = "Handover"
                            runCatching { receipt = repository.request("GET", "/v1/claims/$claimId/receipt") }
                        }
                        runCatching { refresh() }.onFailure {
                            backendOnline = false
                            message = "Review saved. Updates could not refresh; try Refresh updates."
                        }
                    }
                }) { Text("Confirm") } },
                dismissButton = { TextButton(onClick = { pendingDecision = null }) { Text("Cancel") } })
        }
        ConnectedBottomNav(page, role) { destination ->
            page = destination; selected = null; matches = emptyList(); photoBytes = null
        }
    }
}

@Composable
private fun ConnectedReportForm(repository: FirebaseRepository, staff: Boolean, busy: Boolean,
    run: (suspend () -> Unit) -> Unit, initialFound: Boolean = false,
    draftKey: String? = null, onStartAnother: () -> Unit,
    onSaved: suspend (String) -> Unit) {
    val context = LocalContext.current
    val uid = repository.auth.currentUser!!.uid
    val preferences = remember(uid) { context.getSharedPreferences("connected_draft_$uid", android.content.Context.MODE_PRIVATE) }
    val saved = remember(uid, draftKey) {
        runCatching { JSONObject(draftKey?.let { preferences.getString(it, "{}") } ?: "{}") }.getOrDefault(JSONObject())
    }
    var requestId by rememberSaveable { mutableStateOf(saved.optString("request_id", UUID.randomUUID().toString())) }
    var storageKey by rememberSaveable { mutableStateOf(draftKey ?: "draft_$requestId") }
    var found by rememberSaveable(initialFound) { mutableStateOf(initialFound || saved.optBoolean("found")) }
    var title by rememberSaveable { mutableStateOf(saved.optString("title")) }
    var description by rememberSaveable { mutableStateOf(saved.optString("description")) }
    var category by rememberSaveable { mutableStateOf(saved.optString("category")) }
    var location by rememberSaveable { mutableStateOf(saved.optString("location")) }
    var date by rememberSaveable { mutableStateOf(saved.optString("date")) }
    var brand by rememberSaveable { mutableStateOf(saved.optString("brand")) }
    var colour by rememberSaveable { mutableStateOf(saved.optString("colour")) }
    var model by rememberSaveable { mutableStateOf(saved.optString("model")) }
    var material by rememberSaveable { mutableStateOf(saved.optString("material")) }
    var photo by rememberSaveable { mutableStateOf(saved.optString("photo")) }
    var privateFeature by rememberSaveable { mutableStateOf(saved.optString("private_feature")) }
    var custodyLocation by rememberSaveable { mutableStateOf(saved.optString("custody_location")) }
    var sealed by rememberSaveable { mutableStateOf(saved.optBoolean("sealed")) }
    var formStep by rememberSaveable { mutableIntStateOf(1) }
    var attempted by rememberSaveable { mutableStateOf(saved.optBoolean("attempted")) }
    var completed by remember { mutableStateOf(false) }
    fun persistDraft() {
        val draft = JSONObject().put("request_id", requestId).put("found", found).put("title", title)
            .put("description", description).put("category", category).put("location", location).put("date", date)
            .put("brand", brand).put("colour", colour).put("model", model).put("material", material)
            .put("photo", photo).put("private_feature", privateFeature).put("custody_location", custodyLocation)
            .put("sealed", sealed).put("attempted", attempted)
            .put("updated_at", System.currentTimeMillis())
        preferences.edit().putString(storageKey, draft.toString()).apply()
    }
    SideEffect { if (!completed && (title.isNotBlank() || description.isNotBlank() || category.isNotBlank()
            || location.isNotBlank() || attempted)) persistDraft() }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            context.contentResolver.takePersistableUriPermission(uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            photo = uri.toString()
        }
    }
    Text(if (formStep == 1) if (found) "Found item check-in" else "Report lost item" else if (found && category in listOf("wallet", "documents")) "Sensitive item check-in" else "Category details",
        color = Color(0xFF112B4B), fontSize = 22.sp, fontWeight = FontWeight.Bold)
    Text(if (formStep == 1) "STEP 1 OF 2 · ITEM DETAILS" else "STEP 2 OF 2 · PRIVATE DETAILS",
        color = Color(0xFF245ECA), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    LinearProgressIndicator(progress = { if (formStep == 1) 0.5f else 1f }, modifier = Modifier.fillMaxWidth())
    Text("Your progress is saved on this device.", color = Color(0xFF697586), fontSize = 12.sp)
    if (attempted) ConnectedNotice("Submission not confirmed",
        "Retry this report unchanged, or edit a copy with a new request ID. Check My reports before submitting a copy to avoid duplicates.")
    if (attempted) TextButton(enabled = !busy, onClick = {
        requestId = UUID.randomUUID().toString()
        storageKey = "draft_$requestId"
        attempted = false
        formStep = 1
    }) { Text("Edit a copy as a new report") }
    OutlinedButton(enabled = !busy, onClick = onStartAnother, modifier = Modifier.fillMaxWidth()) {
        Text("Start another report")
    }
    if (staff && formStep == 1) Row { Checkbox(found, { if (!attempted) found = it }, enabled = !busy && !attempted); Text("Record found item in office custody") }
    val editable = !busy && !attempted
    if (formStep == 1) {
    OutlinedTextField(title, { title = it }, enabled = editable, label = { Text("Item name") }, modifier = Modifier.fillMaxWidth())
    OutlinedTextField(description, { description = it }, enabled = editable,
        label = { Text("General description") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
    Text("Keep IDs, passwords and hidden identifying details out of this description.", color = Color(0xFF697586), fontSize = 12.sp)
    if (editable) {
        CategoryPicker(category, { category = it })
        CampusLocationPicker(location, { location = it })
        ReportDatePicker(date, { date = it })
    } else Text("$category Â· $location Â· $date")
    Button(enabled = !busy && title.isNotBlank() && description.length >= 3 && category.isNotBlank() && location.isNotBlank() && validReportDate(date),
        onClick = { formStep = 2 }, modifier = Modifier.fillMaxWidth()) {
        Text(if (attempted) "Review and retry submission" else "Continue to category details")
    }
    } else {
    ConnectedNotice("${category.replace('_', ' ').replaceFirstChar { it.uppercase() }} selected",
        if (found) "Record custody carefully. Private details stay with office staff." else "Add a detail that may help the office verify your report.")
    OutlinedTextField(privateFeature, { privateFeature = it.take(200) }, enabled = editable,
        label = { Text(if (found) "Private condition or custody detail" else "Hidden identifying feature (optional)") },
        modifier = Modifier.fillMaxWidth(), minLines = 2)
    OutlinedTextField(brand, { brand = it }, enabled = editable, label = { Text("Brand (optional)") })
    OutlinedTextField(model, { model = it }, enabled = editable, label = { Text("Model (optional)") })
    OutlinedTextField(colour, { colour = it }, enabled = editable, label = { Text("Colour (optional)") })
    OutlinedTextField(material, { material = it }, enabled = editable, label = { Text("Material (optional)") })
    if (found && category in listOf("wallet", "documents")) {
        ConnectedNotice("Restricted office intake", "Seal wallets and documents on receipt. Never put personal numbers in a public description.")
        OutlinedTextField(custodyLocation, { custodyLocation = it.take(100) }, enabled = editable,
            label = { Text("Secure custody location") }, modifier = Modifier.fillMaxWidth())
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(sealed, { sealed = it }, enabled = editable)
            Text("Item sealed and recorded", color = Color(0xFF112B4B))
        }
    }
    if (staff) {
        ConnectedNotice("Photo uploads unavailable", "This Firebase project needs a Storage billing upgrade. You can submit the report without a photo.")
    }
    if (photo.isNotBlank()) {
        Text("A photo is saved in this draft but cannot be uploaded yet. Remove it to submit this report without a photo.", color = Color(0xFF697586), fontSize = 12.sp)
        TextButton(enabled = editable, onClick = { photo = "" }) { Text("Remove saved photo") }
    }
    Button(enabled = !busy && photo.isBlank() && title.isNotBlank() && description.length >= 3 && category.isNotBlank() && location.isNotBlank() && validReportDate(date)
        && (!found || category !in listOf("wallet", "documents") || (sealed && custodyLocation.isNotBlank())), onClick = {
        attempted = true
        persistDraft()
        run {
            val photoPath = ""
            val payload = JSONObject().put("request_id", requestId).put("report_type", if (found) "found" else "lost")
                .put("item_name", title).put("description", description).put("category_id", category)
                .put("location_id", location).put("event_date", date).put("brand", brand).put("model", model)
                .put("colour", colour).put("material", material).put("photo_reference", photoPath)
                .put("distinctive_features", JSONArray().also { if (privateFeature.isNotBlank()) it.put(privateFeature) })
                .put("sealed", sealed).put("custody_location", custodyLocation)
            val saved = repository.request("POST", "/v1/reports", payload)
            completed = true
            preferences.edit().remove(storageKey).apply()
            onSaved(saved.getString("id"))
        }
    }) { Text(if (attempted) "Retry same submission" else "Submit report") }
    if (attempted) Text("If connection failed, retry uses the same request ID to avoid duplicates. Check Reports before starting a different submission.")
    TextButton(onClick = { formStep = 1 }) { Text("Back to item details") }
    }
}
