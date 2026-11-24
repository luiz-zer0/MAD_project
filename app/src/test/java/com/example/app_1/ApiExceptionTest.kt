package com.example.app_1.data

import org.junit.Assert.assertEquals
import org.junit.Test

class ApiExceptionTest {
    @Test fun retainsHttpStatusMessageAndFieldErrors() {
        val error = ApiException(422, "description: too short", mapOf("description" to "too short"))

        assertEquals(422, error.statusCode)
        assertEquals("description: too short", error.userMessage)
        assertEquals("too short", error.fieldErrors["description"])
    }
}
