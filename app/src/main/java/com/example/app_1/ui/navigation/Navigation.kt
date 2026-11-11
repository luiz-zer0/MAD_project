package com.example.app_1.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.ui.graphics.vector.ImageVector

enum class UserRole {
    STUDENT,
    OFFICE_STAFF,
    TECHNICAL_ADMIN,
}

enum class Screen {
    SIGN_IN,
    CHECK_INBOX,
    HOME,
    REPORT_LOST,
    REPORT_FOUND,
    CATEGORY_DETAILS,
    MY_REPORTS,
    POSSIBLE_MATCHES,
    CANDIDATE_DETAILS,
    CLAIM_STATUS,
    NOTIFICATIONS,
    OFFICE_DASHBOARD,
    FOUND_ITEM_CHECK_IN,
    SENSITIVE_ITEM_CHECK_IN,
    INVENTORY,
    CLAIMS_QUEUE,
    OWNERSHIP_VERIFICATION,
    PICKUP_HANDOVER,
    UNCLAIMED_ITEMS,
    CATEGORY_RULES,
    ACCESS_AND_AUDIT,
}

enum class StudentNavDestination(
    val label: String,
    val icon: ImageVector,
    val screen: Screen,
) {
    HOME("Home", Icons.Default.Home, Screen.HOME),
    REPORTS("Reports", Icons.AutoMirrored.Filled.List, Screen.MY_REPORTS),
    MATCHES("Matches", Icons.Default.Search, Screen.POSSIBLE_MATCHES),
    ALERTS("Alerts", Icons.Default.Notifications, Screen.NOTIFICATIONS),
}

enum class StaffNavDestination(
    val label: String,
    val icon: ImageVector,
    val screen: Screen,
) {
    OFFICE("Office", Icons.Default.Dashboard, Screen.OFFICE_DASHBOARD),
    INVENTORY("Inventory", Icons.Default.Inventory, Screen.INVENTORY),
    CLAIMS("Claims", Icons.AutoMirrored.Filled.Assignment, Screen.CLAIMS_QUEUE),
    UNCLAIMED("Unclaimed", Icons.Default.Home, Screen.UNCLAIMED_ITEMS),
}

enum class AdminNavDestination(
    val label: String,
    val icon: ImageVector,
    val screen: Screen,
) {
    RULES("Rules", Icons.Default.Tune, Screen.CATEGORY_RULES),
    AUDIT("Audit", Icons.Default.Security, Screen.ACCESS_AND_AUDIT),
}
