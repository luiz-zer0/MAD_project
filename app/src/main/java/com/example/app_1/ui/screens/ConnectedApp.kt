package com.example.app_1.ui.screens

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.app_1.BuildConfig
import com.example.app_1.data.FirebaseRepository
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

private fun JSONObject.rows(key: String): List<JSONObject> {
    val array = optJSONArray(key) ?: JSONArray()
    return (0 until array.length()).map { array.getJSONObject(it) }
}

/** Connected workflow. The old sample screens remain available in source, not as authorization. */
@Composable
fun ConnectedApp() {
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
    var password by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var page by rememberSaveable { mutableStateOf("Reports") }
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
            try { action() } catch (e: Exception) { message = e.message ?: "Connection failed. Try again." }
            finally { busy = false }
        }
    }
    suspend fun refresh() {
        reports = repository.request("GET", "/v1/reports").rows("reports")
        claims = repository.request("GET", "/v1/claims").rows("claims")
        notifications = repository.request("GET", "/v1/notifications").rows("notifications")
    }
    LaunchedEffect(Unit) {
        if (repository.auth.currentUser != null) run {
            if (repository.checkEmailVerified()) {
                identity = repository.refreshIdentity()
                refresh()
            } else message = "Check your inbox and spam folder for the Firebase verification link."
        }
    }
    Column(Modifier.fillMaxSize().safeDrawingPadding().imePadding().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Strathmore Lost & Found", style = MaterialTheme.typography.headlineSmall)
        if (BuildConfig.USE_FIREBASE_EMULATORS) Text("Local emulator â€¢ fictional test accounts only")
        if (busy) LinearProgressIndicator(Modifier.fillMaxWidth())
        if (message.isNotBlank()) Text(message, style = MaterialTheme.typography.bodyMedium)
        if (identity == null) {
            Text("Sign in with your verified university account. Staff access is assigned by an administrator.")
            OutlinedTextField(email, { email = it }, label = { Text("University email") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(password, { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
            Button(enabled = !busy, onClick = { run {
                repository.signIn(email, password)
                password = ""
                if (repository.checkEmailVerified()) {
                    identity = repository.refreshIdentity()
                    refresh()
                } else message = "Email not verified yet. Check your inbox, then tap Check verification."
            } }) { Text("Sign in") }
            OutlinedButton(enabled = !busy, onClick = { run {
                repository.register(email, password)
                password = ""
                message = "Verification email sent. Verify it, then press Check verification."
            } }) { Text("Create student account") }
            if (repository.auth.currentUser != null) {
                TextButton(enabled = !busy, onClick = { run {
                    if (repository.checkEmailVerified()) {
                        identity = repository.refreshIdentity()
                        refresh()
                    } else message = "Still waiting for verification. Check spam or resend the email."
                } }) { Text("Check verification") }
                TextButton(enabled = !busy, onClick = { run {
                    repository.resendVerificationEmail()
                    message = "Verification email requested again. Check your inbox and spam folder."
                } }) { Text("Resend verification email") }
            }
        } else {
            val staff = identity!!.optString("role") in listOf("office_staff", "admin")
            Text("${repository.auth.currentUser?.email} Â· ${identity!!.optString("role")}")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(enabled = !busy, onClick = { run { refresh() } }) { Text("Refresh") }
                TextButton(enabled = !busy, onClick = {
                    repository.auth.signOut(); identity = null; reports = emptyList(); matches = emptyList()
                    claims = emptyList(); notifications = emptyList(); selected = null; photoBytes = null; page = "Reports"
                }) { Text("Sign out") }
            }
            Row {
                listOf("Reports", "New", "Claims", "Alerts").forEach { title ->
                    TextButton(enabled = !busy, onClick = { page = title; selected = null; matches = emptyList(); photoBytes = null }) { Text(title) }
                }
            }
            when (page) {
                "New" -> key(identity!!.getString("uid")) {
                    ConnectedReportForm(repository, staff, busy, { action -> run(action) }) {
                        message = "Report saved: $it. Matching will run separately."
                        page = "Reports"
                        refresh()
                    }
                }
                "Reports" -> {
                    Text(if (staff) "Office reports" else "My reports", style = MaterialTheme.typography.titleLarge)
                    Text("Showing up to 100 reports per type. Refresh to check matching progress.")
                    if (reports.isEmpty()) Text("No saved reports yet.")
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
                    Text("Notifications", style = MaterialTheme.typography.titleLarge)
                    if (notifications.isEmpty()) Text("No notifications yet.")
                    notifications.forEach { Text(it.optString("message")) }
                }
            }
        }
    }
}

@Composable
private fun ConnectedReportForm(repository: FirebaseRepository, staff: Boolean, busy: Boolean,
    run: (suspend () -> Unit) -> Unit, onSaved: suspend (String) -> Unit) {
    val context = LocalContext.current
    val uid = repository.auth.currentUser!!.uid
    val preferences = remember(uid) { context.getSharedPreferences("connected_draft_$uid", android.content.Context.MODE_PRIVATE) }
    val saved = remember(uid) { runCatching { JSONObject(preferences.getString("draft", "{}")!!) }.getOrDefault(JSONObject()) }
    var requestId by rememberSaveable { mutableStateOf(saved.optString("request_id", UUID.randomUUID().toString())) }
    var found by rememberSaveable { mutableStateOf(saved.optBoolean("found")) }
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
