package com.example.app_1.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

enum class Screen {
    HOME,
    REPORT_LOST,
    REPORT_FOUND,
    REPORTS,
    MATCHES,
    ALERTS
}

enum class AppNavDestination(
    val label: String,
    val icon: ImageVector,
    val screen: Screen,
) {
    HOME("Home", Icons.Default.Home, Screen.HOME),
    REPORTS("Reports", Icons.AutoMirrored.Filled.List, Screen.REPORTS),
    MATCHES("Matches", Icons.Default.Search, Screen.MATCHES),
    ALERTS("Alerts", Icons.Default.Notifications, Screen.ALERTS),
}
