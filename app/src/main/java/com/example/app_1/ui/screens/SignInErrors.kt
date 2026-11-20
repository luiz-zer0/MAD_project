package com.example.app_1.ui.screens

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthException
import java.net.UnknownHostException

internal fun signInError(error: Exception): String = signInErrorMessage(
    code = (error as? FirebaseAuthException)?.errorCode,
    detail = error.message.orEmpty(),
    networkFailure = error is FirebaseNetworkException || error is UnknownHostException,
    rateLimited = error is FirebaseTooManyRequestsException,
)

internal fun signInErrorMessage(
    code: String? = null,
    detail: String = "",
    networkFailure: Boolean = false,
    rateLimited: Boolean = false,
): String {
    // Some SDK versions expose the email quota as a generic FirebaseException.
    // Check this before the general rate-limit case: retrying will not reset a daily quota.
    if (code == "ERROR_QUOTA_EXCEEDED" ||
        detail.contains("Exceeded daily quota for email sign-in", ignoreCase = true)) {
        return "The app has reached Firebase's daily sign-in email limit. Try again after the quota resets. The project owner can increase the limit by upgrading the Firebase billing plan."
    }
    if (networkFailure) {
        return "Could not reach Firebase sign-in. Check your connection and try again."
    }
    if (rateLimited || code == "ERROR_TOO_MANY_REQUESTS") {
        return "Too many attempts. Please wait a moment and try again."
    }
    return when (code) {
        "ERROR_INVALID_EMAIL" -> "Check your email address and try again."
        "ERROR_EXPIRED_ACTION_CODE", "ERROR_INVALID_ACTION_CODE" -> "This link has expired. Request a new one."
        "ERROR_OPERATION_NOT_ALLOWED" -> "Email sign-in is unavailable right now. Please try again later."
        else -> "Something went wrong. Please try again."
    }
}