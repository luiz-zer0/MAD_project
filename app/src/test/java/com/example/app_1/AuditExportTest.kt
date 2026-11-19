package com.example.app_1

import com.example.app_1.ui.screens.RedactedAuditRow
import com.example.app_1.ui.screens.redactedAuditCsv
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class AuditExportTest {
    @Test
    fun exportContainsOnlyRedactedFieldsAndEscapesCells() {
        val csv = redactedAuditCsv(listOf(RedactedAuditRow("approved", "=wallet", "2026-11-19")))
        assertEquals("action,category,time\n\"approved\",\"'=wallet\",\"2026-11-19\"\n", csv.replace("\r\n", "\n"))
        assertFalse(csv.contains("actor_uid"))
        assertFalse(csv.contains("claim_id"))
    }
}
