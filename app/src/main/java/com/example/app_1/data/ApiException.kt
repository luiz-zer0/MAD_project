package com.example.app_1.data

import java.io.IOException

/** An HTTP failure with a safe message the connected UI can show or route by status. */
class ApiException(
    val statusCode: Int,
    val userMessage: String,
    val fieldErrors: Map<String, String> = emptyMap()
) : IOException(userMessage)
