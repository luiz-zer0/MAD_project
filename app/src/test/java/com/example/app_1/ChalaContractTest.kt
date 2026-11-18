package com.example.app_1

import com.example.app_1.ui.screens.*
import org.junit.Assert.*
import org.junit.Test

class ChalaContractTest {
    @Test fun catalogIsCompleteAndReversible() {
        listOf(ChalaCatalog.categories to 18, ChalaCatalog.locations to 12, ChalaCatalog.idTypes to 3).forEach { (entries, count) ->
            assertEquals(count, entries.size)
            assertEquals(count, entries.map { it.id }.toSet().size)
            entries.forEach { assertEquals(it, ChalaCatalog.resolve(entries, it.sourceLabel)) }
        }
        assertNotEquals(ChalaCatalog.resolve(ChalaCatalog.locations, "MSB"), ChalaCatalog.resolve(ChalaCatalog.locations, "MSB 1"))
        assertNull(ChalaCatalog.resolve(ChalaCatalog.locations, "Library"))
        assertNull(ChalaCatalog.resolve(ChalaCatalog.locations, "Auditorium"))
    }
    @Test fun legacyInputIsPreservedAndCannotBeSubmittedWithoutClassification() {
        val old = BasicReportDraft(false, "Other", "old description", "2026-11-10", "Library")
        assertEquals("Library", old.location)
        assertEquals("", old.itemName)
        assertNull(old.toGeneralRequest())
    }
    @Test fun fiveFieldsAreRequiredButPhotosAndPrivateCluesAreNot() {
        val report = BasicReportDraft(false, "Keys", "Three keys", "2026-11-10", "MSB", "Key ring")
        val request = report.toGeneralRequest()!!
        assertEquals("Key ring", request["item_name"])
        assertEquals("msb", request["location_id"])
        assertFalse(request.containsKey("report_id"))
        assertFalse(request.containsKey("photo_reference"))
        assertNull(report.copy(itemName = "").toGeneralRequest())
        assertNull(report.copy(date = "2026-02-30").toGeneralRequest())
    }
}
