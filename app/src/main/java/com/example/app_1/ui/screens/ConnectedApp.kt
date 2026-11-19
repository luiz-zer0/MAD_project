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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_1.BuildConfig
import com.example.app_1.R
import com.example.app_1.data.FirebaseRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

private fun JSONObject.rows(key: String): List<JSONObject> {
    val array = optJSONArray(key) ?: JSONArray()
    return (0 until array.length()).map { array.getJSONObject(it) }
}

private fun signInError(error: Exception): String = when {
    error is FirebaseNetworkException || error is java.net.UnknownHostException ->
        "You’re offline. Reconnect and try again."
    else -> when ((error as? FirebaseAuthException)?.errorCode) {
    "ERROR_INVALID_EMAIL" -> "Check your email address and try again."
    "ERROR_TOO_MANY_REQUESTS" -> "Too many attempts. Please wait a moment and try again."
    "ERROR_EXPIRED_ACTION_CODE", "ERROR_INVALID_ACTION_CODE" -> "This link has expired. Request a new one."
    "ERROR_OPERATION_NOT_ALLOWED" -> "Email sign-in is unavailable right now. Please try again later."
    else -> "Something went wrong. Please try again."
    }
}

/** Connected workflow. The old sample screens remain available in source, not as authorization. */
@Composable
fun ConnectedApp(incomingLink: String? = null, onLinkConsumed: () -> Unit = {}) {
    val context = LocalContext.current
    val configured = BuildConfig.FIREBASE_PROJECT_ID.isNotBlank() && BuildConfig.FIREBASE_APP_ID.isNotBlank() &&
        BuildConfig.FIREBASE_API_KEY.isNotBlank() && BuildConfig.ML_API_BASE_URL.isNotBlank()
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
    var busy by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var messageIsSuccess by remember { mutableStateOf(false) }
    val signInScroll = rememberScrollState()
    var page by rememberSaveable { mutableStateOf("Home") }
    var backendOnline by remember { mutableStateOf(false) }
    var reports by remember { mutableStateOf(emptyList<JSONObject>()) }
    var matches by remember { mutableStateOf(emptyList<JSONObject>()) }
    var claims by remember { mutableStateOf(emptyList<JSONObject>()) }
    var notifications by remember { mutableStateOf(emptyList<JSONObject>()) }
    var selected by remember { mutableStateOf<JSONObject?>(null) }
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
                    backendOnline = false
                    if (page == "New" || page == "NewFound")
                        "Couldn't submit the report. Your draft is saved on this device."
                    else "Updates are unavailable right now. Please try again."
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
    LaunchedEffect(Unit) {
        if (repository.auth.currentUser != null) run {
            if (repository.auth.currentUser?.isEmailVerified == true || repository.checkEmailVerified()) {
                identity = repository.localIdentity()
                page = "Home"
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
            try {
                if (!repository.isEmailSignInLink(incomingLink)) {
                    message = "This link has expired. Request a new one."
                } else {
                    repository.completeEmailLink(incomingLink, email)
                    identity = repository.localIdentity()
                    page = "Home"
                    runCatching { refresh() }.onFailure { backendOnline = false }
                }
            } finally { onLinkConsumed() }
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
                        onClick = { run { identity = repository.localIdentity(); page = "Home"; runCatching { refresh() } } }) {
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
                Row(Modifier.fillMaxWidth().background(Color(0xFFEDF3FE), RoundedCornerShape(10.dp)).padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(painterResource(R.drawable.signin_shield_check), null, tint = Color.Unspecified)
                    Text("Only @strathmore.edu addresses can receive a sign-in link.",
                        color = Color(0xFF102B49), fontSize = 13.sp, lineHeight = 19.sp)
                }
                if (!incomingLink.isNullOrBlank() && !repository.hasPendingEmailLink()) {
                    Text("Enter the email address that received the link to finish signing in.",
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
        if (page == "Home") {
            ConnectedHomeScreen(
                email = repository.auth.currentUser?.email.orEmpty(), role = role,
                reports = reports, backendOnline = backendOnline,
                onLost = { page = "New" },
                onFound = { page = if (role == "student") "FoundHelp" else "NewFound" },
                onReports = { page = "Reports" }, onMatches = { page = "Matches" },
                onProfile = { page = "Profile" }, modifier = Modifier.weight(1f))
        } else {
    Column(Modifier.weight(1f).imePadding().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(when (page) {
            "Reports" -> if (role == "student") "My reports" else "Office reports"
            "Matches" -> "Possible matches"
            "Alerts" -> "Notifications"
            "FoundHelp" -> "I found something"
            "Profile" -> "Your account"
            else -> "Report an item"
        }, color = Color(0xFF112B4B), fontSize = 22.sp, fontWeight = FontWeight.Bold)
        if (BuildConfig.USE_FIREBASE_EMULATORS) Text("Local emulator · fictional test accounts only")
        if (busy) LinearProgressIndicator(Modifier.fillMaxWidth())
        if (message.isNotBlank()) Text(message, style = MaterialTheme.typography.bodyMedium)
        if (identity != null) {
            val staff = identity!!.optString("role") in listOf("office_staff", "admin")
            if (page in setOf("Reports", "Matches", "Alerts", "Claims"))
                TextButton(enabled = !busy, onClick = { run { refresh() } }) { Text("Refresh updates") }
            when (page) {
                "Profile" -> {
                    Text(repository.auth.currentUser?.email.orEmpty())
                    Text("Role: ${if (staff) if (role == "admin") "Administrator" else "Office staff" else "Student"}")
                    Text("Staff access is assigned by an administrator.")
                    OutlinedButton(onClick = {
                        repository.auth.signOut(); identity = null; page = "Home"
                        reports = emptyList(); matches = emptyList(); claims = emptyList(); notifications = emptyList()
                    }) { Text("Sign out") }
                }
                "New", "NewFound" -> key(identity!!.getString("uid"), page) {
                    ConnectedReportForm(repository, staff, busy, { action -> run(action) },
                        initialFound = page == "NewFound") {
                        message = "Report saved: $it. Matching will run separately."
                        page = "Reports"
                        refresh()
                    }
                }
                "FoundHelp" -> {
                    Text("Please bring the item to the Lost & Found Office in the Student Centre. Office staff record found items so they can verify custody and arrange a safe handover.")
                    Button(onClick = { page = "Home" }) { Text("Back to Home") }
                }
                "Matches" -> {
                    Text("Suggestions are reviewed by staff. A score does not establish ownership.")
                    if (!backendOnline) Text("Matches are temporarily unavailable.")
                    else if (reports.none { it.optString("report_type") == "lost" }) Text("Report a lost item to see possible matches.")
                    else reports.filter { it.optString("report_type") == "lost" }.forEach { row ->
                        Card(onClick = { selected = row; page = "Reports"; run {
                            matches = repository.request("GET", "/v1/reports/lost/${row.getString("id")}/matches").rows("matches")
                        } }, modifier = Modifier.fillMaxWidth()) {
                            Text(row.optString("item_name"), Modifier.padding(16.dp))
                        }
                    }
                }
                "Reports" -> {
                    if (!backendOnline) Text("Reports are temporarily unavailable. You can still prepare a draft on this device.")
                    else if (reports.isEmpty()) Text("No reports yet.")
                    Button(onClick = { page = "New" }) { Text("Report a lost item") }
                    reports.forEach { row ->
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(row.optString("item_name"), style = MaterialTheme.typography.titleMedium)
                                Text("${row.optString("report_type")} Â· ${row.optString("status")} Â· matching ${row.optString("matching_status")}")
                                Text(row.optString("description"))
                                TextButton(enabled = !busy, onClick = { run {
                                    selected = row; photoBytes = null
                                    matches = repository.request("GET", "/v1/reports/${row.getString("report_type")}/${row.getString("id")}/matches").rows("matches")
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
                    if (selected != null) {
                        Text("Selected: ${selected!!.optString("item_name")}", style = MaterialTheme.typography.titleMedium)
                        photoBytes?.let { bytes ->
                            val bitmap = remember(bytes) { BitmapFactory.decodeByteArray(bytes, 0, bytes.size) }
                            if (bitmap != null) Image(bitmap.asImageBitmap(), "Report photograph", Modifier.fillMaxWidth().heightIn(max = 280.dp))
                        }
                        Text("Model scores rank suggestions; they do not establish ownership. Office staff verify every claim.")
                        if (matches.isEmpty()) Text("No suggestions available yet.")
                        matches.forEach { match ->
                            Text("${match.optString("item_name")} · text similarity ${"%.3f".format(match.optDouble("score"))}")
                            Text(match.optString("explanation", "Staff review is required before any ownership decision."))
                            if (match.optString("owner_uid") == identity!!.getString("uid")) Button(enabled = !busy, onClick = { run {
                                repository.request("POST", "/v1/matches/${match.getString("id")}/claim", JSONObject())
                                message = "Claim submitted for staff verification."; refresh()
                            } }) { Text("Request ownership review") }
                        }
                    }
                }
                "Claims" -> {
                    Text("Ownership review", style = MaterialTheme.typography.titleLarge)
                    if (claims.isEmpty()) Text("No claims yet.")
                    claims.forEach { claim ->
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(12.dp)) {
                                Text("Claim ${claim.getString("id").take(12)} Â· ${claim.getString("status")}")
                                Text("Lost report: ${claim.getString("lost_id")}")
                                Text("Found report: ${claim.getString("found_id")}")
                                if (staff) {
                                    Text("Verify ownership in person before approving; confirm physical handover before marking collected.")
                                    val actions = when (claim.getString("status")) {
                                        "pending" -> listOf("approved", "rejected")
                                        "approved" -> listOf("collected", "rejected")
                                        else -> emptyList()
                                    }
                                    actions.forEach { action ->
                                        TextButton(enabled = !busy, onClick = { run {
                                            repository.request("POST", "/v1/claims/${claim.getString("id")}/decision", JSONObject().put("status", action))
                                            message = "Review saved."; refresh()
                                        } }) { Text("Mark $action") }
                                    }
                                }
                            }
                        }
                    }
                }
                "Alerts" -> {
                    if (!backendOnline) Text("Notifications are temporarily unavailable.")
                    else if (notifications.isEmpty()) Text("No notifications yet.")
                    notifications.forEach { Text(it.optString("message")) }
                }
            }
        }
    }
        }
        ConnectedBottomNav(page, role) { destination ->
            page = destination; selected = null; matches = emptyList(); photoBytes = null
        }
    }
}

@Composable
private fun ConnectedReportForm(repository: FirebaseRepository, staff: Boolean, busy: Boolean,
    run: (suspend () -> Unit) -> Unit, initialFound: Boolean = false,
    onSaved: suspend (String) -> Unit) {
    val context = LocalContext.current
    val uid = repository.auth.currentUser!!.uid
    val preferences = remember(uid) { context.getSharedPreferences("connected_draft_$uid", android.content.Context.MODE_PRIVATE) }
    val saved = remember(uid) { runCatching { JSONObject(preferences.getString("draft", "{}")!!) }.getOrDefault(JSONObject()) }
    var requestId by rememberSaveable { mutableStateOf(saved.optString("request_id", UUID.randomUUID().toString())) }
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
    var attempted by rememberSaveable { mutableStateOf(saved.optBoolean("attempted")) }
    var completed by remember { mutableStateOf(false) }
    fun persistDraft() {
        val draft = JSONObject().put("request_id", requestId).put("found", found).put("title", title)
            .put("description", description).put("category", category).put("location", location).put("date", date)
            .put("brand", brand).put("colour", colour).put("model", model).put("material", material)
            .put("photo", photo).put("attempted", attempted)
        preferences.edit().putString("draft", draft.toString()).apply()
    }
    SideEffect { if (!completed) persistDraft() }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            context.contentResolver.takePersistableUriPermission(uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            photo = uri.toString()
        }
    }
    Text("New report", style = MaterialTheme.typography.titleLarge)
    Text("Draft saved on this device for your account. It is submitted only after the server confirms.")
    Text("Do not include ID numbers or private ownership evidence. Photos are for staff review, not ML analysis.")
    if (staff) Row { Checkbox(found, { if (!attempted) found = it }, enabled = !busy && !attempted); Text("Record found item in office custody") }
    val editable = !busy && !attempted
    OutlinedTextField(title, { title = it }, enabled = editable, label = { Text("Item name") }, modifier = Modifier.fillMaxWidth())
    OutlinedTextField(description, { description = it }, enabled = editable, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
    if (editable) {
        CategoryPicker(category, { category = it })
        CampusLocationPicker(location, { location = it })
        ReportDatePicker(date, { date = it })
    } else Text("$category Â· $location Â· $date")
    OutlinedTextField(brand, { brand = it }, enabled = editable, label = { Text("Brand (optional)") })
    OutlinedTextField(model, { model = it }, enabled = editable, label = { Text("Model (optional)") })
    OutlinedTextField(colour, { colour = it }, enabled = editable, label = { Text("Colour (optional)") })
    OutlinedTextField(material, { material = it }, enabled = editable, label = { Text("Material (optional)") })
    OutlinedButton(enabled = editable, onClick = { picker.launch(arrayOf("image/jpeg", "image/png", "image/webp")) }) {
        Text(if (photo.isBlank()) "Add photo" else "Replace photo")
    }
    if (photo.isNotBlank()) TextButton(enabled = editable, onClick = { photo = "" }) { Text("Remove photo") }
    Button(enabled = !busy && title.isNotBlank() && description.length >= 3 && category.isNotBlank() && location.isNotBlank() && validReportDate(date), onClick = {
        attempted = true
        persistDraft()
        run {
            val photoPath = if (photo.isBlank()) "" else repository.uploadPhoto(requestId, android.net.Uri.parse(photo), context)
            val payload = JSONObject().put("request_id", requestId).put("report_type", if (found) "found" else "lost")
                .put("item_name", title).put("description", description).put("category_id", category)
                .put("location_id", location).put("event_date", date).put("brand", brand).put("model", model)
                .put("colour", colour).put("material", material).put("photo_reference", photoPath)
            val saved = repository.request("POST", "/v1/reports", payload)
            completed = true
            preferences.edit().remove("draft").apply()
            onSaved(saved.getString("id"))
        }
    }) { Text(if (attempted) "Retry same submission" else "Submit report") }
    if (attempted) Text("If connection failed, retry uses the same request ID to avoid duplicates. Check Reports before starting a different submission.")
    if (attempted) TextButton(enabled = !busy, onClick = { requestId = UUID.randomUUID().toString(); attempted = false }) {
        Text("Edit as a new submission")
    }
}
