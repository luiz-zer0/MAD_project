package com.example.app_1.data

import android.content.Context
import android.net.Uri
import com.example.app_1.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/** Credentials come from Firebase Auth. No service-account key belongs in the APK. */
class FirebaseRepository(context: Context) {
    private val preferences = context.getSharedPreferences("email_link_sign_in", Context.MODE_PRIVATE)
    private val app: FirebaseApp = FirebaseApp.getApps(context).firstOrNull() ?: FirebaseApp.initializeApp(
        context, FirebaseOptions.Builder()
            .setProjectId(BuildConfig.FIREBASE_PROJECT_ID)
            .setApplicationId(BuildConfig.FIREBASE_APP_ID)
 .setGcmSenderId(BuildConfig.FIREBASE_APP_ID.split(":").getOrElse(1) { "" })
            .setApiKey(BuildConfig.FIREBASE_API_KEY)
            .setStorageBucket(BuildConfig.FIREBASE_STORAGE_BUCKET).build())
    val auth: FirebaseAuth = FirebaseAuth.getInstance(app)
    private val storage = FirebaseStorage.getInstance(app)

    suspend fun sendEmailLink(email: String) {
        val address = email.trim().lowercase()
 require(address.endsWith("@strathmore.edu")) { "Use your Strathmore email address." }
        val settings = ActionCodeSettings.newBuilder()
 .setUrl("https://${BuildConfig.FIREBASE_PROJECT_ID}.firebaseapp.com/finishSignIn")
            .setHandleCodeInApp(true)
            .setAndroidPackageName("com.example.app_1", false, null)
            .build()
        auth.sendSignInLinkToEmail(address, settings).await()
        preferences.edit().putString("pending_email", address).apply()
    }

    fun isEmailSignInLink(link: String): Boolean = auth.isSignInWithEmailLink(link)

    fun hasPendingEmailLink(): Boolean = !preferences.getString("pending_email", null).isNullOrBlank()

    suspend fun completeEmailLink(link: String, enteredEmail: String = "") {
        require(isEmailSignInLink(link)) { "This is not a valid sign-in link." }
        val email = preferences.getString("pending_email", null) ?: enteredEmail.trim().lowercase()
        require(email.endsWith("@strathmore.edu")) { "Enter the Strathmore email that received this link." }
        auth.signInWithEmailLink(email, link).await()
        preferences.edit().remove("pending_email").apply()
    }

    init {
        if (BuildConfig.USE_FIREBASE_EMULATORS) {
            check(BuildConfig.DEBUG && BuildConfig.FIREBASE_PROJECT_ID.startsWith("demo-"))
            auth.val host = URL(BuildConfig.ML_API_BASE_URL).host         check(host in setOf("10.0.2.2", "127.0.0.1"))         auth.useEmulator(host, 9099)         storage.useEmulator(host, 9199) 9099)
            storage.val host = URL(BuildConfig.ML_API_BASE_URL).host         check(host in setOf("10.0.2.2", "127.0.0.1"))         auth.useEmulator(host, 9099)         storage.useEmulator(host, 9199) 9199)
        }
    }

    suspend fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email.trim().lowercase(), password).await()
    }

    suspend fun register(email: String, password: String) {
        require(email.trim().lowercase().endsWith("@strathmore.edu")) { "Use your Strathmore email." }
        auth.createUserWithEmailAndPassword(email.trim().lowercase(), password).await()
        auth.currentUser!!.sendEmailVerification().await()
    }

    suspend fun checkEmailVerified(): Boolean {
        val user = auth.currentUser ?: error("Sign in to check your verification status.")
        user.reload().await()
        return user.isEmailVerified
    }

    suspend fun resendVerificationEmail() {
        val user = auth.currentUser ?: error("Sign in to resend the email.")
        user.reload().await()
        if (!user.isEmailVerified) user.sendEmailVerification().await()
    }

    suspend fun refreshIdentity(): JSONObject {
        auth.currentUser?.reload()?.await()
        auth.currentUser?.getIdToken(true)?.await()
        return request("GET", "/v1/me")
    }

    /** Navigation can start from the verified Firebase session while the reports API is offline. */
    suspend fun localIdentity(): JSONObject {
        val user = auth.currentUser ?: error("Sign in first.")
        require(user.isEmailVerified && user.email.orEmpty().endsWith("@strathmore.edu", true)) {
            "A verified university email is required."
        }
        val claim = user.getIdToken(false).await().claims["role"] as? String
        val role = claim?.takeIf { it in setOf("student", "office_staff", "admin") } ?: "student"
        return JSONObject().put("uid", user.uid).put("role", role)
    }

    suspend fun registerPush() {
        if (!BuildConfig.USE_FIREBASE_EMULATORS) {
            com.google.firebase.messaging.FirebaseMessaging.getInstance().isAutoInitEnabled = true
            val token = com.google.firebase.messaging.FirebaseMessaging.getInstance().token.await()
            request("POST", "/v1/devices", JSONObject().put("token", token))
        }
    }

    suspend fun uploadPhoto(requestId: String, uri: Uri, context: Context): String {
        val uid = requireNotNull(auth.currentUser).uid
        val mime = context.contentResolver.getType(uri)
        require(mime in listOf("image/jpeg", "image/png", "image/webp")) { "Choose a JPEG, PNG or WebP image." }
        val path = "report_photos/$uid/$requestId/photo"
        val ref = storage.reference.child(path)
        // Retried submissions reuse the same immutable object after a successful upload.
        val alreadyUploaded = runCatching { ref.metadata.await() }.isSuccess
        if (!alreadyUploaded) ref.putFile(uri).await()
        return path
    }

    suspend fun photo(path: String): ByteArray = storage.reference.child(path).getBytes(5L * 1024 * 1024).await()

    suspend fun request(method: String, path: String, body: JSONObject? = null): JSONObject {
        var forceRefresh = false
        repeat(2) { attempt ->
            val token = auth.currentUser?.getIdToken(forceRefresh)?.await()?.token
                ?: throw ApiException(401, "Sign in first.")
            try {
                return requestWithToken(method, path, body, token)
            } catch (error: ApiException) {
                if (error.statusCode != 401 || attempt > 0) throw error
                forceRefresh = true
            }
        }
        throw ApiException(401, "Your sign-in expired. Sign in again and retry.")
    }

    private suspend fun requestWithToken(method: String, path: String, body: JSONObject?, token: String): JSONObject =
        withContext(Dispatchers.IO) {
            val baseUrl = BuildConfig.ML_API_BASE_URL.trimEnd('/')
            if (baseUrl.isBlank()) throw IOException("The Lost & Found service URL is not configured.")
            val url = URL(baseUrl + path)
            val debugLoopback = BuildConfig.DEBUG && url.protocol == "http" &&
                url.host in setOf("127.0.0.1", "10.0.2.2")
            if (url.protocol != "https" && !debugLoopback) {
                throw IOException("The deployed API must use HTTPS; debug builds may use local loopback.")
            }
            val connection = url.openConnection() as HttpURLConnection
            try {
                connection.requestMethod = method
                connection.connectTimeout = 15000
                connection.readTimeout = 20000
                connection.instanceFollowRedirects = false
                connection.setRequestProperty("Authorization", "Bearer $token")
                if (body != null) {
                    connection.doOutput = true
                    connection.setRequestProperty("Content-Type", "application/json")
                    connection.outputStream.use { it.write(body.toString().toByteArray(Charsets.UTF_8)) }
                }
                val status = connection.responseCode
                val text = (if (status in 200..299) connection.inputStream else connection.errorStream)
                    ?.bufferedReader()?.use { it.readText() }.orEmpty()
                if (status !in 200..299) throw apiException(status, text)
                if (text.isBlank()) JSONObject() else JSONObject(text)
            } finally {
                connection.disconnect()
            }
        }

    private fun apiException(status: Int, response: String): ApiException {
        val detail = runCatching { JSONObject(response).opt("detail") }.getOrNull()
        val fields = linkedMapOf<String, String>()
        val message = when (detail) {
            is org.json.JSONArray -> {
                for (index in 0 until detail.length()) {
                    val item = detail.optJSONObject(index) ?: continue
                    val path = item.optJSONArray("loc")
                    val field = if (path == null || path.length() == 0) "request" else
                        (1 until path.length()).joinToString(".") { path.optString(it) }.ifBlank { "request" }
                    val error = item.optString("msg", "Invalid value")
                    fields[field] = error
                }
                fields.entries.joinToString("; ") { "${it.key}: ${it.value}" }
                    .ifBlank { "The request contains invalid information." }
            }
            is String -> detail.takeIf { it.isNotBlank() } ?: defaultApiMessage(status)
            else -> defaultApiMessage(status)
        }
        return ApiException(status, message, fields)
    }

    private fun defaultApiMessage(status: Int): String = when (status) {
        401 -> "Your sign-in expired. Sign in again and retry."
        403 -> "Your account does not have permission for this action."
        404 -> "The requested item is no longer available."
        409 -> "This request conflicts with a newer update. Refresh and review it."
        422 -> "Check the highlighted information and try again."
        in 500..599 -> "The service could not complete this request. Try again shortly."
        else -> "The request failed (HTTP $status)."
    }
}
