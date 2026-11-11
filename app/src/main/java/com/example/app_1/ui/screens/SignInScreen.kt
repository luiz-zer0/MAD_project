package com.example.app_1.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_1.ui.navigation.UserRole
import com.example.app_1.ui.theme.App_1Theme

@Composable
fun SignInScreen(
    onSendLink: (String, UserRole) -> Unit,
    modifier: Modifier = Modifier,
    initialEmail: String = "amina@strathmore.edu",
) {
    var email by rememberSaveable { mutableStateOf(initialEmail) }
    var selectedRole by rememberSaveable { mutableStateOf(UserRole.STUDENT) }

    val isInvalidEmail = email.isNotBlank() && !email.lowercase().endsWith("@strathmore.edu")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        // Decorative Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color(0xFF0F2942)),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        tint = Color(0xFFEAB308),
                        modifier = Modifier.size(32.dp),
                    )
                }
                Text(
                    text = "Sign in",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }
        }

        // Form Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Use your Strathmore University email. We'll send a one-time sign-in link to your inbox.",
                fontSize = 13.sp,
                color = Color(0xFF475569),
                lineHeight = 18.sp,
            )

            // Role Picker Section
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Select account role",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F2942),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    RoleOptionCard(
                        title = "Student",
                        subtitle = "Amina",
                        isSelected = selectedRole == UserRole.STUDENT,
                        onClick = {
                            selectedRole = UserRole.STUDENT
                            email = "amina@strathmore.edu"
                        },
                        modifier = Modifier.weight(1f),
                    )
                    RoleOptionCard(
                        title = "Office Staff",
                        subtitle = "Leah Mugo",
                        isSelected = selectedRole == UserRole.OFFICE_STAFF,
                        onClick = {
                            selectedRole = UserRole.OFFICE_STAFF
                            email = "leah.mugo@strathmore.edu"
                        },
                        modifier = Modifier.weight(1f),
                    )
                    RoleOptionCard(
                        title = "Tech Admin",
                        subtitle = "Rina Nali",
                        isSelected = selectedRole == UserRole.TECHNICAL_ADMIN,
                        onClick = {
                            selectedRole = UserRole.TECHNICAL_ADMIN
                            email = "rina.nali@strathmore.edu"
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFF1F5F9))

            // Email Field
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "University email",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F2942),
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("name@strathmore.edu", fontSize = 13.sp, color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isInvalidEmail) Color(0xFFDC2626) else Color(0xFF1D4ED8),
                        unfocusedBorderColor = if (isInvalidEmail) Color(0xFFFCA5A5) else Color(0xFFCBD5E1),
                    ),
                )
            }

            // Validation Banners
            if (isInvalidEmail) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = "Use an address ending in @strathmore.edu.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF991B1B),
                        modifier = Modifier.padding(14.dp),
                    )
                }
            } else {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF1D4ED8),
                            modifier = Modifier.size(16.dp),
                        )
                        Text(
                            text = "Only @strathmore.edu addresses can receive a sign-in link.",
                            fontSize = 12.sp,
                            color = Color(0xFF1E40AF),
                            lineHeight = 16.sp,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Button
            Button(
                onClick = { onSendLink(email, selectedRole) },
                enabled = email.isNotBlank() && !isInvalidEmail,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8)),
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Email me a sign-in link",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun RoleOptionCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFEFF6FF) else Color.White,
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) Color(0xFF1D4ED8) else Color(0xFFE2E8F0),
        ),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isSelected) Color(0xFF1D4ED8) else Color(0xFF0F2942),
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = Color.Gray,
            )
        }
    }
}

@Preview(showBackground = true, name = "Sign In View")
@Composable
fun SignInPreview() {
    App_1Theme {
        SignInScreen(onSendLink = { _, _ -> })
    }
}
