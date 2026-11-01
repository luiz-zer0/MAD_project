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
import com.example.app_1.ui.navigation.AppNavDestination
import com.example.app_1.ui.navigation.Screen
import com.example.app_1.ui.screens.CategoryDetailsScreen
import com.example.app_1.ui.screens.HomeScreen
import com.example.app_1.ui.screens.MyReportsScreen
import com.example.app_1.ui.screens.PossibleMatchesScreen
import com.example.app_1.ui.screens.ReportFoundScreen
import com.example.app_1.ui.screens.ReportLostScreen
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

    val activeNavDestination = when (currentScreen) {
        Screen.HOME -> AppNavDestination.HOME
        Screen.REPORT_LOST, Screen.REPORT_FOUND, Screen.CATEGORY_DETAILS, Screen.MY_REPORTS, Screen.REPORTS -> AppNavDestination.REPORTS
        Screen.POSSIBLE_MATCHES, Screen.MATCHES -> AppNavDestination.MATCHES
        Screen.ALERTS -> AppNavDestination.ALERTS
    }

    Scaffold(
        bottomBar = {
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
                )
                Screen.POSSIBLE_MATCHES, Screen.MATCHES -> PossibleMatchesScreen(
                    onBack = { currentScreen = Screen.MY_REPORTS },
                )
                Screen.ALERTS -> HomeScreen(
                    onNavigateToReportLost = { currentScreen = Screen.REPORT_LOST },
                    onNavigateToReportFound = { currentScreen = Screen.REPORT_FOUND },
                )
            }
        }
    }
}
