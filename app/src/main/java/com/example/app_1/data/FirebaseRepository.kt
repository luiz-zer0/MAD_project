package com.example.app_1.data

import android.content.Context
import android.net.Uri
import com.example.app_1.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/** Credentials come from Firebase Auth. No service-account key belongs in the APK. */
class FirebaseRepository(context: Context) {
    private val app: FirebaseApp = FirebaseApp.getApps(context).firstOrNull() ?: FirebaseApp.initializeApp(
        context, FirebaseOptions.Builder()
            .setProjectId(BuildConfig.FIREBASE_PROJECT_ID)
            .setApplicationId(BuildConfig.FIREBASE_APP_ID)
            .setGcmSenderId(BuildConfig.FIREBASE_APP_ID.split(":").getOrElse(1) { "" })
            .setApiKey(BuildConfig.FIREBASE_API_KEY)
            .setStorageBucket(BuildConfig.FIREBASE_STORAGE_BUCKET).build())
    val auth: FirebaseAuth = FirebaseAuth.getInstance(app)
    private val storage = FirebaseStorage.getInstance(app)

    init {
        if (BuildConfig.USE_FIREBASE_EMULATORS) {
            check(BuildConfig.DEBUG && BuildConfig.FIREBASE_PROJECT_ID.startsWith("demo-"))
            auth.useEmulator("10.0.2.2", 9099)
            storage.useEmulator("10.0.2.2", 9199)
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
        val token = auth.currentUser?.getIdToken(false)?.await()?.token ?: error("Sign in first.")
        return withContext(Dispatchers.IO) {
            val url = URL(BuildConfig.ML_API_BASE_URL.trimEnd('/') + path)
            val debugLoopback = BuildConfig.DEBUG && url.protocol == "http" &&
                url.host in setOf("127.0.0.1", "10.0.2.2")
            check(url.protocol == "https" || debugLoopback) {
                "The deployed API must use HTTPS; debug builds may use local loopback."
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
                val success = connection.responseCode in 200..299
                val text = (if (success) connection.inputStream else connection.errorStream)
                    ?.bufferedReader()?.use { it.readText() }.orEmpty()
                val result = if (text.isBlank()) JSONObject() else JSONObject(text)
                if (!success) error(result.optString("detail", "Request failed (${connection.responseCode})"))
                result
            } finally {
                connection.disconnect()
            }
        }
    }
}
