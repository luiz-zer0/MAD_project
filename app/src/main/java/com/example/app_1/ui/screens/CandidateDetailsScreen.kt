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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_1.R
import com.example.app_1.ui.theme.App_1Theme

@Composable
fun CandidateDetailsScreen(
    onBack: () -> Unit,
    onSubmitClaim: () -> Unit,
    modifier: Modifier = Modifier,
    candidate: CandidateUiModel = sampleCandidates.first(),
) {
    var hiddenFeature by remember(candidate.id) { mutableStateOf("") }
    var twoThingsInside by remember(candidate.id) { mutableStateOf("") }
    var showErrors by remember(candidate.id) { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF0F2942),
                )
            }
            Text(
                text = "Candidate details",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF0F2942),
            )
            TextButton(onClick = onBack) {
                Text(
                    text = "Not mine",
                    color = Color(0xFF1D4ED8),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        HorizontalDivider(color = Color(0xFFF1F5F9))

        // Main Scrollable Body
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Item Card Overview
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFEFF6FF)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_home),
                            contentDescription = null,
                            tint = Color(0xFF1D4ED8),
                            modifier = Modifier.size(32.dp),
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFDCFCE7),
                    ) {
                        Text(
                            text = candidate.status,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF15803D),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        )
                    }

                    Text(
                        text = candidate.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF0F2942),
                    )
                    Text(
                        text = "${candidate.foundDate}\n${candidate.location}",
                        fontSize = 12.sp,
                        color = Color(0xFF475569),
                        lineHeight = 16.sp,
                    )
                    Text(
                        text = "General details only • unique identifiers hidden",
                        fontSize = 10.sp,
                        color = Color(0xFF475569),
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            ) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text("Category: ${candidate.category}", color = Color(0xFF0F2942), fontSize = 12.sp)
                    Text(candidate.description, color = Color(0xFF0F2942), fontSize = 12.sp)
                    Text("Date reported: ${candidate.reportedAt}", color = Color(0xFF475569), fontSize = 11.sp)
                    Text("Why it appears: ${candidate.matchReason}", color = Color(0xFF475569), fontSize = 11.sp)
                    Text(candidate.similarityScore?.let { "Similarity score: $it% · suggestion only" }
                        ?: "Similarity score unavailable", color = Color(0xFF475569), fontSize = 11.sp)
                    Text("Image unavailable", color = Color(0xFF475569), fontSize = 11.sp)
                }
            }

            // Questions Header Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color(0xFF1D4ED8),
                            modifier = Modifier.size(18.dp),
                        )
                        Text(
                            text = "Is this yours?",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF0F2942),
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFEFF6FF),
                        ) {
                            Text(
                                text = "PRIVATE • REQUIRED",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1D4ED8),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            )
                        }
                    }

                    Text(
                        text = "Enter private details only the owner would know. This preview does not send answers to staff.",
                        fontSize = 12.sp,
                        color = Color(0xFF475569),
                        lineHeight = 16.sp,
                    )

                    // Question 1
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Describe a hidden identifying feature",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF0F2942),
                        )
                        OutlinedTextField(
                            value = hiddenFeature,
                            onValueChange = { hiddenFeature = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            isError = showErrors && hiddenFeature.isBlank(),
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF0F2942)),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF1D4ED8),
                                unfocusedBorderColor = Color(0xFFCBD5E1),
                            ),
                        )
                        if (showErrors && hiddenFeature.isBlank()) Text("Required", color = Color(0xFFB42318))
                    }

                    // Question 2
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Name at least two things inside",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF0F2942),
                        )
                        OutlinedTextField(
                            value = twoThingsInside,
                            onValueChange = { twoThingsInside = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            isError = showErrors && twoThingsInside.isBlank(),
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF0F2942)),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF1D4ED8),
                                unfocusedBorderColor = Color(0xFFCBD5E1),
                            ),
                        )
                        if (showErrors && twoThingsInside.isBlank()) Text("Required", color = Color(0xFFB42318))
                    }
                }
            }

            // Info Notice
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFF1D4ED8),
                        modifier = Modifier.size(18.dp),
                    )
                    Column {
                        Text(
                            text = "Office review comes first",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF1E40AF),
                        )
                        Text(
                            text = "A possible match does not establish ownership. Claim submission needs a connected staff review service.",
                            fontSize = 11.sp,
                            color = Color(0xFF1E40AF),
                            lineHeight = 15.sp,
                        )
                    }
                }
            }

            // Submit Claim Button
            Button(
                onClick = {
                    showErrors = true
                    if (hiddenFeature.isNotBlank() && twoThingsInside.isNotBlank()) onSubmitClaim()
                },
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
                        text = "Preview claim status",
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

            Text(
                text = "Prototype only: no claim or ownership answers are sent. If a future claim is approved, bring your campus ID for in-person handover.",
                fontSize = 11.sp,
                color = Color(0xFF475569),
                lineHeight = 15.sp,
            )
        }
    }
}

@Preview(showBackground = true, name = "Candidate Details View")
@Composable
fun CandidateDetailsPreview() {
    App_1Theme {
        CandidateDetailsScreen(onBack = {}, onSubmitClaim = {})
    }
}
