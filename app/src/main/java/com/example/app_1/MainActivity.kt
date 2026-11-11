package com.example.app_1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_1.ui.navigation.AdminNavDestination
import com.example.app_1.ui.navigation.Screen
import com.example.app_1.ui.navigation.StaffNavDestination
import com.example.app_1.ui.navigation.StudentNavDestination
import com.example.app_1.ui.navigation.UserRole
import com.example.app_1.ui.screens.AccessAndAuditScreen
import com.example.app_1.ui.screens.CandidateDetailsScreen
import com.example.app_1.ui.screens.CategoryDetailsScreen
import com.example.app_1.ui.screens.CategoryRulesScreen
import com.example.app_1.ui.screens.CheckInboxScreen
import com.example.app_1.ui.screens.ClaimStatusScreen
import com.example.app_1.ui.screens.ClaimsQueueScreen
import com.example.app_1.ui.screens.FoundItemCheckInScreen
import com.example.app_1.ui.screens.HomeScreen
import com.example.app_1.ui.screens.InventoryScreen
import com.example.app_1.ui.screens.MyReportsScreen
import com.example.app_1.ui.screens.NotificationsScreen
import com.example.app_1.ui.screens.OfficeDashboardScreen
import com.example.app_1.ui.screens.OwnershipVerificationScreen
import com.example.app_1.ui.screens.PickupHandoverScreen
import com.example.app_1.ui.screens.PossibleMatchesScreen
import com.example.app_1.ui.screens.ReportFoundScreen
import com.example.app_1.ui.screens.ReportLostScreen
import com.example.app_1.ui.screens.SensitiveItemCheckInScreen
import com.example.app_1.ui.screens.SignInScreen
import com.example.app_1.ui.screens.UnclaimedItemsScreen
import com.example.app_1.ui.theme.App_1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App_1Theme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    var currentScreen by rememberSaveable { mutableStateOf(Screen.SIGN_IN) }
    var currentRole by rememberSaveable { mutableStateOf(UserRole.STUDENT) }

    val showBottomBar = currentScreen != Screen.SIGN_IN && currentScreen != Screen.CHECK_INBOX

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp,
                ) {
                    when (currentRole) {
                        UserRole.STUDENT -> {
                            StudentNavDestination.entries.forEach { destination ->
                                val isSelected = when (destination) {
                                    StudentNavDestination.HOME -> currentScreen == Screen.HOME
                                    StudentNavDestination.REPORTS -> currentScreen == Screen.MY_REPORTS || currentScreen == Screen.REPORT_LOST || currentScreen == Screen.REPORT_FOUND || currentScreen == Screen.CATEGORY_DETAILS
                                    StudentNavDestination.MATCHES -> currentScreen == Screen.POSSIBLE_MATCHES || currentScreen == Screen.CANDIDATE_DETAILS || currentScreen == Screen.CLAIM_STATUS
                                    StudentNavDestination.ALERTS -> currentScreen == Screen.NOTIFICATIONS
                                }
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = { currentScreen = destination.screen },
                                    icon = {
                                        Icon(
                                            imageVector = destination.icon,
                                            contentDescription = destination.label,
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = destination.label,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color(0xFF1D4ED8),
                                        selectedTextColor = Color(0xFF1D4ED8),
                                        unselectedIconColor = Color.Gray,
                                        unselectedTextColor = Color.Gray,
                                        indicatorColor = Color(0xFFEFF6FF),
                                    ),
                                )
                            }
                        }

                        UserRole.OFFICE_STAFF -> {
                            StaffNavDestination.entries.forEach { destination ->
                                val isSelected = when (destination) {
                                    StaffNavDestination.OFFICE -> currentScreen == Screen.OFFICE_DASHBOARD
                                    StaffNavDestination.INVENTORY -> currentScreen == Screen.INVENTORY || currentScreen == Screen.FOUND_ITEM_CHECK_IN || currentScreen == Screen.SENSITIVE_ITEM_CHECK_IN
                                    StaffNavDestination.CLAIMS -> currentScreen == Screen.CLAIMS_QUEUE || currentScreen == Screen.OWNERSHIP_VERIFICATION || currentScreen == Screen.PICKUP_HANDOVER
                                    StaffNavDestination.UNCLAIMED -> currentScreen == Screen.UNCLAIMED_ITEMS
                                }
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = { currentScreen = destination.screen },
                                    icon = {
                                        Icon(
                                            imageVector = destination.icon,
                                            contentDescription = destination.label,
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = destination.label,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color(0xFF1D4ED8),
                                        selectedTextColor = Color(0xFF1D4ED8),
                                        unselectedIconColor = Color.Gray,
                                        unselectedTextColor = Color.Gray,
                                        indicatorColor = Color(0xFFEFF6FF),
                                    ),
                                )
                            }
                        }

                        UserRole.TECHNICAL_ADMIN -> {
                            AdminNavDestination.entries.forEach { destination ->
                                val isSelected = when (destination) {
                                    AdminNavDestination.RULES -> currentScreen == Screen.CATEGORY_RULES
                                    AdminNavDestination.AUDIT -> currentScreen == Screen.ACCESS_AND_AUDIT
                                }
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = { currentScreen = destination.screen },
                                    icon = {
                                        Icon(
                                            imageVector = destination.icon,
                                            contentDescription = destination.label,
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = destination.label,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color(0xFF1D4ED8),
                                        selectedTextColor = Color(0xFF1D4ED8),
                                        unselectedIconColor = Color.Gray,
                                        unselectedTextColor = Color.Gray,
                                        indicatorColor = Color(0xFFEFF6FF),
                                    ),
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (currentScreen) {
                Screen.SIGN_IN -> SignInScreen(
                    onSendLink = { _, role ->
                        currentRole = role
                        currentScreen = Screen.CHECK_INBOX
                    },
                )
                Screen.CHECK_INBOX -> CheckInboxScreen(
                    onResendLink = {
                        currentScreen = when (currentRole) {
                            UserRole.STUDENT -> Screen.HOME
                            UserRole.OFFICE_STAFF -> Screen.OFFICE_DASHBOARD
                            UserRole.TECHNICAL_ADMIN -> Screen.CATEGORY_RULES
                        }
                    },
                )
                Screen.HOME -> HomeScreen(
                    onNavigateToReportLost = { currentScreen = Screen.REPORT_LOST },
                    onNavigateToReportFound = { currentScreen = Screen.REPORT_FOUND },
                    onNavigateToLatestUpdate = { currentScreen = Screen.POSSIBLE_MATCHES },
                    onNavigateToOfficeInfo = {
                        currentRole = UserRole.OFFICE_STAFF
                        currentScreen = Screen.OFFICE_DASHBOARD
                    },
                )
                Screen.REPORT_LOST -> ReportLostScreen(
                    onBack = { currentScreen = Screen.HOME },
                    onContinueToDetails = { currentScreen = Screen.CATEGORY_DETAILS },
                )
                Screen.REPORT_FOUND -> ReportFoundScreen(
                    onBack = { currentScreen = Screen.HOME },
                    onContinueToDetails = { currentScreen = Screen.CATEGORY_DETAILS },
                )
                Screen.CATEGORY_DETAILS -> CategoryDetailsScreen(
                    onBack = { currentScreen = Screen.REPORT_LOST },
                    onSubmitReport = { currentScreen = Screen.MY_REPORTS },
                )
                Screen.MY_REPORTS -> MyReportsScreen(
                    onNavigateToNewReport = { currentScreen = Screen.REPORT_LOST },
                    onReviewMatches = { currentScreen = Screen.POSSIBLE_MATCHES },
                    onSelectReportItem = { currentScreen = Screen.CLAIM_STATUS },
                    onContinueDraft = { currentScreen = Screen.CATEGORY_DETAILS },
                )
                Screen.POSSIBLE_MATCHES -> PossibleMatchesScreen(
                    onBack = { currentScreen = Screen.MY_REPORTS },
                )
                Screen.CANDIDATE_DETAILS -> CandidateDetailsScreen(
                    onBack = { currentScreen = Screen.POSSIBLE_MATCHES },
                    onSubmitClaim = { currentScreen = Screen.CLAIM_STATUS },
                )
                Screen.CLAIM_STATUS -> ClaimStatusScreen(
                    onBack = { currentScreen = Screen.POSSIBLE_MATCHES },
                    onBackToReports = { currentScreen = Screen.MY_REPORTS },
                )
                Screen.NOTIFICATIONS -> NotificationsScreen(
                    onSelectNotification = { currentScreen = Screen.CLAIM_STATUS },
                )
                Screen.OFFICE_DASHBOARD -> OfficeDashboardScreen(
                    onCheckInFoundItem = { currentScreen = Screen.FOUND_ITEM_CHECK_IN },
                    onOpenClaimsQueue = { currentScreen = Screen.CLAIMS_QUEUE },
                )
                Screen.FOUND_ITEM_CHECK_IN -> FoundItemCheckInScreen(
                    onContinueToEvidence = { currentScreen = Screen.SENSITIVE_ITEM_CHECK_IN },
                )
                Screen.SENSITIVE_ITEM_CHECK_IN -> SensitiveItemCheckInScreen(
                    onConfirmCheckIn = { currentScreen = Screen.INVENTORY },
                )
                Screen.INVENTORY -> InventoryScreen(
                    onSelectItem = { currentScreen = Screen.OWNERSHIP_VERIFICATION },
                )
                Screen.CLAIMS_QUEUE -> ClaimsQueueScreen(
                    onOpenVerification = { currentScreen = Screen.OWNERSHIP_VERIFICATION },
                )
                Screen.OWNERSHIP_VERIFICATION -> OwnershipVerificationScreen(
                    onApprovePickup = { currentScreen = Screen.PICKUP_HANDOVER },
                )
                Screen.PICKUP_HANDOVER -> PickupHandoverScreen(
                    onViewReceipt = { currentScreen = Screen.UNCLAIMED_ITEMS },
                )
                Screen.UNCLAIMED_ITEMS -> UnclaimedItemsScreen(
                    onSendPlanForReview = { currentScreen = Screen.CATEGORY_RULES },
                )
                Screen.CATEGORY_RULES -> CategoryRulesScreen(
                    onSaveRules = { currentScreen = Screen.ACCESS_AND_AUDIT },
                )
                Screen.ACCESS_AND_AUDIT -> AccessAndAuditScreen(
                    onExportLog = { currentScreen = Screen.SIGN_IN },
                )
            }
        }
    }
}
