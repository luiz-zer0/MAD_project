package com.example.app_1.ui.screens

import android.content.Context

/** Stores only the general first-step fields on this device; private answers are never persisted. */
internal data class BasicReportDraft(
    val isFound: Boolean,
    val category: String,
    val description: String,
    val date: String,
    val location: String,
    val itemName: String = "",
    val photoReference: String = "",
)

internal object ReportDraftStore {
    private const val PREFS = "report_draft"

    fun save(context: Context, draft: BasicReportDraft): Boolean {
        val prefix = if (draft.isFound) "found_" else "lost_"
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putBoolean("${prefix}exists", true)
            .putString("${prefix}item_name", draft.itemName)
            .putString("${prefix}photo_reference", draft.photoReference)
            .putString("${prefix}category", draft.category)
            .putString("${prefix}description", draft.description)
            .putString("${prefix}date", draft.date)
            .putString("${prefix}location", draft.location)
            .commit()
    }

    fun load(context: Context, isFound: Boolean): BasicReportDraft? {
        val prefix = if (isFound) "found_" else "lost_"
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (!prefs.getBoolean("${prefix}exists", false)) return null
        return BasicReportDraft(
            isFound,
            prefs.getString("${prefix}category", "").orEmpty(),
            prefs.getString("${prefix}description", "").orEmpty(),
            prefs.getString("${prefix}date", "").orEmpty(),
            prefs.getString("${prefix}location", "").orEmpty(),
            prefs.getString("${prefix}item_name", "").orEmpty(),
            prefs.getString("${prefix}photo_reference", "").orEmpty(),
        )
    }

    fun loadAll(context: Context): List<BasicReportDraft> =
        listOfNotNull(load(context, false), load(context, true))
}
