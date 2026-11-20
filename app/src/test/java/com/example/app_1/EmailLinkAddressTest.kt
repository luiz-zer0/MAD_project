package com.example.app_1

import com.example.app_1.data.emailForSignInLink
import org.junit.Assert.assertEquals
import org.junit.Test

class EmailLinkAddressTest {
    @Test fun explicitAddressOverridesOldRequestOnSharedPhone() {
        assertEquals("new@strathmore.edu", emailForSignInLink(" New@Strathmore.edu ", "old@strathmore.edu"))
    }

    @Test fun samePhoneUsesAddressSavedWhenLinkWasSent() {
        assertEquals("student@strathmore.edu", emailForSignInLink("", "student@strathmore.edu"))
    }
}
