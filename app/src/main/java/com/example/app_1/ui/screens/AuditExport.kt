package com.example.app_1.ui.screens

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore

internal data class RedactedAuditRow(val action: String, val category: String, val time: String)

private fun csvCell(value: String): String {
    val safe = if (value.firstOrNull() in listOf('=', '+', '-', '@', '\t', '\r')) "'$value" else value
    return "\"${safe.replace("\"", "\"\"").replace("\r", " ").replace("\n", " ")}\""
}

internal fun redactedAuditCsv(rows: List<RedactedAuditRow>): String = buildString {
    appendLine("action,category,time")
    rows.forEach { row ->
        appendLine(listOf(row.action, row.category, row.time).joinToString(",", transform = ::csvCell))
    }
}

/** Saves only action, category and time to the device's Downloads collection. */
internal fun saveAuditCsvToDownloads(context: Context, csv: String): String {
    require(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { "Downloads export requires Android 10 or newer" }
    val name = "lost-found-audit-${System.currentTimeMillis()}.csv"
    val values = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/LostAndFound")
        put(MediaStore.MediaColumns.IS_PENDING, 1)
    }
    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
        ?: error("Could not create the Downloads file")
    try {
        resolver.openOutputStream(uri)?.use { it.write(csv.toByteArray(Charsets.UTF_8)) }
            ?: error("Could not write the Downloads file")
        resolver.update(uri, ContentValues().apply { put(MediaStore.MediaColumns.IS_PENDING, 0) }, null, null)
    } catch (error: Exception) {
        resolver.delete(uri, null, null)
        throw error
    }
    return name
}
