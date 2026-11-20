package com.example.app_1.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SignInErrorsTest {
    @Test fun dailyEmailQuotaExplainsWhyRetryingDoesNotHelp() {
        val message = signInErrorMessage(detail = "This project's quota for this operation has been exceeded. [ Exceeded daily quota for email sign-in. ]")
        assertTrue(message.contains("daily sign-in email limit"))
        assertTrue(message.contains("quota resets"))
    }

    @Test fun structuredQuotaErrorGetsSameExplanation() {
        assertTrue(signInErrorMessage(code = "ERROR_QUOTA_EXCEEDED").contains("daily sign-in email limit"))
    }

    @Test fun dailyQuotaTakesPriorityOverTemporaryThrottling() {
        assertTrue(signInErrorMessage(detail = "Exceeded daily quota for email sign-in.", rateLimited = true)
            .contains("quota resets"))
    }

    @Test fun temporaryThrottlingAndNetworkErrorsStayDistinct() {
        assertEquals("Too many attempts. Please wait a moment and try again.",
            signInErrorMessage(rateLimited = true))
        assertTrue(signInErrorMessage(networkFailure = true).contains("Could not reach Firebase"))
    }

    @Test fun unknownErrorsDoNotExposePrivateDetails() {
        assertEquals("Something went wrong. Please try again.",
            signInErrorMessage(detail = "Sensitive diagnostic details"))
    }
}