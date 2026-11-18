package com.example.app_1.ui.screens

import java.text.SimpleDateFormat
import java.util.Locale

internal fun validReportDate(value: String): Boolean {
    if (!Regex("\\d{4}-\\d{2}-\\d{2}").matches(value)) return false
    return runCatching { SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).apply { isLenient = false }.parse(value) != null }.getOrDefault(false)
}

/** No report_id exists until a durable server acknowledgement. No private clues in this payload. */
internal fun BasicReportDraft.toGeneralRequest(): Map<String, String>? {
    val categoryId = ChalaCatalog.resolve(ChalaCatalog.categories, category)?.id ?: return null
    val locationId = ChalaCatalog.resolve(ChalaCatalog.locations, location)?.id ?: return null
    if (itemName.isBlank() || description.isBlank() || !validReportDate(date)) return null
    return mapOf("report_type" to if (isFound) "found" else "lost", "item_kind" to "general",
        "item_name" to itemName.trim(), "description" to description.trim(), "category_id" to categoryId,
        "location_id" to locationId, "event_date" to date, "catalog_version" to ChalaCatalog.version)
    // Local photo URI is not an uploaded photo_reference. Upload requires a backend first.
}

internal fun descriptionExample(category: String): String = when (ChalaCatalog.resolve(ChalaCatalog.categories, category)?.id) {
    "metal_water_bottle", "plastic_water_bottle", "mugs" -> "Example: blue bottle, metal body, scratched lid."
    "spectacles" -> "Example: black rectangular glasses with a bent arm."
    "spectacle_cases" -> "Example: brown hard glasses case with a worn clasp."
    "keys" -> "Example: three keys on a round ring. Keep private identifiers for staff review."
    "electronics" -> "Example: black Samsung phone with a cracked corner."
    "shoes", "clothing" -> "Example: red trainers with white soles."
    "umbrella" -> "Example: blue folding umbrella with a wooden handle."
    "bags", "wallet", "pencil_pouch" -> "Example: navy fabric bag with a damaged zip."
    "documents" -> "Describe the document cover. Use Lost IDs for identity cards; omit personal numbers."
    "jewelry", "accessories" -> "Describe colour, material and visible features."
    "money" -> "Describe the circumstances; keep the exact amount for staff verification."
    else -> "Describe visible colour, material, brand and distinctive features you actually know."
}
