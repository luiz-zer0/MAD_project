package com.example.app_1.ui.screens
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ReportLostScreen(onBack: () -> Unit, modifier: Modifier = Modifier,
    onContinueToDetails: (String) -> Unit = {}, category: String = "",
    onCategoryChange: (String) -> Unit = {}, itemName: String = "",
    onItemNameChange: (String) -> Unit = {}, onLostId: () -> Unit = {}, onMyReports: () -> Unit = {}) {
    GeneralReportScreen(false, onBack, modifier, category, onCategoryChange, itemName, onItemNameChange, onLostId, onMyReports)
}
