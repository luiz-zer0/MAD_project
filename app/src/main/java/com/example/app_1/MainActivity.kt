package com.example.app_1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_1.ui.navigation.AppNavDestination
import com.example.app_1.ui.navigation.Screen
import com.example.app_1.ui.screens.CategoryDetailsScreen
import com.example.app_1.ui.screens.CandidateDetailScreen
import com.example.app_1.ui.screens.ClaimStatusScreen
import com.example.app_1.ui.screens.FoundItemCheckInScreen
import com.example.app_1.ui.screens.HomeScreen
import com.example.app_1.ui.screens.MyReportsScreen
import com.example.app_1.ui.screens.NotificationsScreen
import com.example.app_1.ui.screens.OfficeDashboardScreen
import com.example.app_1.ui.screens.PossibleMatchesScreen
import com.example.app_1.ui.screens.ReportFoundScreen
import com.example.app_1.ui.screens.ReportLostScreen
import com.example.app_1.ui.screens.sampleCandidates
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
    var currentScreen by rememberSaveable { mutableStateOf(Screen.HOME) }
    var selectedCandidateId by rememberSaveable { mutableStateOf(sampleCandidates.first().id) }
    var hasClaimPreview by rememberSaveable { mutableStateOf(false) }
    var claimPreviewCandidateId by rememberSaveable { mutableStateOf(sampleCandidates.first().id) }
    var statusReturnScreen by rememberSaveable { mutableStateOf(Screen.CANDIDATE_DETAIL) }
    var notificationReadMask by rememberSaveable { mutableIntStateOf(0) }
    val selectedCandidate = sampleCandidates.firstOrNull { it.id == selectedCandidateId }
        ?: sampleCandidates.first()

    BackHandler(currentScreen == Screen.CANDIDATE_DETAIL || currentScreen == Screen.CLAIM_STATUS) {
        currentScreen = if (currentScreen == Screen.CLAIM_STATUS) statusReturnScreen
        else Screen.POSSIBLE_MATCHES
    }
    BackHandler(currentScreen == Screen.OFFICE_DASHBOARD) { currentScreen = Screen.HOME }
    BackHandler(currentScreen == Screen.FOUND_ITEM_CHECK_IN) {
        currentScreen = Screen.OFFICE_DASHBOARD
    }

    val isOfficeScreen = currentScreen == Screen.OFFICE_DASHBOARD ||
        currentScreen == Screen.FOUND_ITEM_CHECK_IN

    val activeNavDestination = when (currentScreen) {
        Screen.HOME -> AppNavDestination.HOME
        Screen.REPORT_LOST, Screen.REPORT_FOUND, Screen.CATEGORY_DETAILS, Screen.MY_REPORTS, Screen.REPORTS, Screen.CLAIM_STATUS -> AppNavDestination.REPORTS
        Screen.POSSIBLE_MATCHES, Screen.MATCHES, Screen.CANDIDATE_DETAIL -> AppNavDestination.MATCHES
        Screen.ALERTS -> AppNavDestination.ALERTS
        Screen.OFFICE_DASHBOARD, Screen.FOUND_ITEM_CHECK_IN -> AppNavDestination.HOME
    }

    Scaffold(
        bottomBar = {
            if (isOfficeScreen) {
                OfficeBottomBar(
                    isInventorySelected = currentScreen == Screen.FOUND_ITEM_CHECK_IN,
                    onOffice = { currentScreen = Screen.OFFICE_DASHBOARD },
                    onInventory = { currentScreen = Screen.FOUND_ITEM_CHECK_IN },
                )
            } else {
                NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp,
                ) {
                    AppNavDestination.entries.forEach { destination ->
                    NavigationBarItem(
                        selected = activeNavDestination == destination,
                        onClick = {
                            currentScreen = when (destination) {
                                AppNavDestination.HOME -> Screen.HOME
                                AppNavDestination.REPORTS -> Screen.MY_REPORTS
                                AppNavDestination.MATCHES -> Screen.POSSIBLE_MATCHES
                                AppNavDestination.ALERTS -> Screen.ALERTS
                            }
                        },
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
                                fontWeight = if (activeNavDestination == destination) FontWeight.Bold else FontWeight.Normal,
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
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (currentScreen) {
                Screen.HOME -> HomeScreen(
                    onNavigateToReportLost = { currentScreen = Screen.REPORT_LOST },
                    onNavigateToReportFound = { currentScreen = Screen.REPORT_FOUND },
                    onOpenNotifications = { currentScreen = Screen.ALERTS },
                    onOpenOfficePreview = { currentScreen = Screen.OFFICE_DASHBOARD },
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
                Screen.MY_REPORTS, Screen.REPORTS -> MyReportsScreen(
                    onNavigateToNewReport = { currentScreen = Screen.REPORT_LOST },
                    onReviewMatches = { currentScreen = Screen.POSSIBLE_MATCHES },
                    hasClaimPreview = hasClaimPreview,
                    claimPreviewTitle = sampleCandidates.firstOrNull { it.id == claimPreviewCandidateId }?.title
                        ?: sampleCandidates.first().title,
                    onViewClaimStatus = {
                        selectedCandidateId = claimPreviewCandidateId
                        statusReturnScreen = Screen.MY_REPORTS
                        currentScreen = Screen.CLAIM_STATUS
                    },
                )
                Screen.POSSIBLE_MATCHES, Screen.MATCHES -> PossibleMatchesScreen(
                    onBack = { currentScreen = Screen.MY_REPORTS },
                    onViewCandidate = { candidateId ->
                        selectedCandidateId = candidateId
                        currentScreen = Screen.CANDIDATE_DETAIL
                    },
                )
                Screen.CANDIDATE_DETAIL -> CandidateDetailScreen(
                    candidate = selectedCandidate,
                    onBack = { currentScreen = Screen.POSSIBLE_MATCHES },
                    onNotMine = { currentScreen = Screen.POSSIBLE_MATCHES },
                    onPreviewStatus = {
                        hasClaimPreview = true
                        claimPreviewCandidateId = selectedCandidateId
                        statusReturnScreen = Screen.CANDIDATE_DETAIL
                        currentScreen = Screen.CLAIM_STATUS
                    },
                )
                Screen.CLAIM_STATUS -> ClaimStatusScreen(
                    candidate = selectedCandidate,
                    onBack = { currentScreen = statusReturnScreen },
                    onBackToReports = { currentScreen = Screen.MY_REPORTS },
                )
                Screen.ALERTS -> NotificationsScreen(
                    readMask = notificationReadMask,
                    onReadMaskChange = { notificationReadMask = it },
                    hasClaimPreview = hasClaimPreview,
                    onOpenClaim = {
                        selectedCandidateId = claimPreviewCandidateId
                        statusReturnScreen = Screen.ALERTS
                        currentScreen = Screen.CLAIM_STATUS
                    },
                    onReviewMatches = { currentScreen = Screen.POSSIBLE_MATCHES },
                    onOpenReports = { currentScreen = Screen.MY_REPORTS },
                )
                Screen.OFFICE_DASHBOARD -> OfficeDashboardScreen(
                    onCheckInFoundItem = { currentScreen = Screen.FOUND_ITEM_CHECK_IN },
                    onExitPreview = { currentScreen = Screen.HOME },
                )
                Screen.FOUND_ITEM_CHECK_IN -> FoundItemCheckInScreen(
                    onBackToOffice = { currentScreen = Screen.OFFICE_DASHBOARD },
                )
            }
        }
    }
}

@Composable
private fun OfficeBottomBar(
    isInventorySelected: Boolean,
    onOffice: () -> Unit,
    onInventory: () -> Unit,
) {
    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
        listOf(
            Triple("Office", Icons.Default.Home, onOffice),
            Triple("Inventory", Icons.Default.Inventory2, onInventory),
            Triple("Claims", Icons.AutoMirrored.Filled.Assignment, {}),
            Triple("Admin", Icons.Default.Settings, {}),
        ).forEachIndexed { index, (label, icon, onClick) ->
            NavigationBarItem(
                selected = if (isInventorySelected) index == 1 else index == 0,
                enabled = index < 2,
                onClick = onClick,
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label, fontSize = 11.sp) },
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val emailInputLayout = findViewById<TextInputLayout>(R.id.emailInputLayout)
        val emailEditText = findViewById<TextInputEditText>(R.id.emailEditText)
        val passwordInputLayout = findViewById<TextInputLayout>(R.id.passwordInputLayout)
        val passwordEditText = findViewById<TextInputEditText>(R.id.passwordEditText)
        val loginButton = findViewById<MaterialButton>(R.id.loginButton)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()

            emailInputLayout.error = null
            passwordInputLayout.error = null

            var isValid = true

            if (email.isEmpty()) {
                emailInputLayout.error = getString(R.string.error_email_required)
                isValid = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInputLayout.error = getString(R.string.error_email_invalid)
                isValid = false
            }

            if (password.isEmpty()) {
                passwordInputLayout.error = getString(R.string.error_password_required)
                isValid = false
            } else if (password.length < 6) {
                passwordInputLayout.error = getString(R.string.error_password_short)
                isValid = false
            }

            if (isValid) {
                // Demo rule: emails containing "office" or "staff" open the office side
                if (email.lowercase().contains("office") || email.lowercase().contains("staff")) {
                    openOffice("office")
                } else {
                    openStudent("home")
                }
                finish()
            }
        }

        // TODO: "Don't have an account? Sign up" link.
        // Send me your activity_main.xml so I can add it with the right id.
    }
}
