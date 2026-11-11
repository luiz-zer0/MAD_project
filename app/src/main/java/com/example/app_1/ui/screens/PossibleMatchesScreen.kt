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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
fun PossibleMatchesScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    hasMatches: Boolean = true,
    onViewCandidate: (String) -> Unit = {},
) {
    var newestFirst by remember { mutableStateOf(false) }
    val candidates = if (hasMatches) {
        if (newestFirst) sampleCandidates.sortedByDescending { it.reportedAt } else sampleCandidates
    } else emptyList()
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 8.dp, vertical = 8.dp),
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
                text = "Possible matches",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF0F2942),
            )
        }

        HorizontalDivider(color = Color(0xFFF1F5F9))

        // Scrollable Body
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Dark Header Banner Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2942)),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = "SAMPLE LOST REPORT",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEAB308),
                    )
                    Text(
                        text = "Navy backpack",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                    Text(
                        text = "Last seen 14 Oct • Library",
                        fontSize = 12.sp,
                        color = Color(0xFFCBD5E1),
                    )
                }
            }

            // Security Privacy Notice
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
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF1D4ED8),
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = if (hasMatches) {
                            "Only general details are shown. Names, serial numbers, contents and unique marks stay hidden."
                        } else {
                            "Good call reporting now. Office check-in may take up to 7 days."
                        },
                        fontSize = 12.sp,
                        color = Color(0xFF1E40AF),
                        lineHeight = 16.sp,
                    )
                }
            }

            if (hasMatches) {
                // Section Title Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "${candidates.size} items to review",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0F2942),
                    )
                    TextButton(onClick = { newestFirst = !newestFirst }) {
                        Text(
                            text = if (newestFirst) "Newest first" else "Suggested order",
                            fontSize = 12.sp,
                            color = Color(0xFF1D4ED8),
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }

                // Match Candidate 1
                candidates.forEach { candidate ->
                    CandidateItemCard(candidate = candidate, onViewCandidate = onViewCandidate)
                }
            } else {
                // No Matches Yet Section
                Text(
                    text = "No matches yet",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF0F2942),
                )

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "How matching would work",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF0F2942),
                        )
                        Text(
                            text = "With a connected report service, new office items could be checked against active lost reports and reviewed by staff.",
                            fontSize = 12.sp,
                            color = Color(0xFF475569),
                            lineHeight = 17.sp,
                        )
                    }
                }
            }

            // Footer note
            Text(
                text = "Sample candidates only. This prototype does not run live matching or send match alerts.",
                fontSize = 11.sp,
                color = Color(0xFF475569),
                lineHeight = 15.sp,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun CandidateItemCard(
    candidate: CandidateUiModel,
    onViewCandidate: (String) -> Unit,
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFEFF6FF)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_home),
                        contentDescription = null,
                        tint = Color(0xFF1D4ED8),
                        modifier = Modifier.size(28.dp),
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFEFF6FF),
                    ) {
                        Text(
                            text = "Worth a look",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D4ED8),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = candidate.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0F2942),
                    )
                    Text(
                        text = "${candidate.category} • found on campus",
                        fontSize = 12.sp,
                        color = Color(0xFF475569),
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color(0xFF475569),
                    modifier = Modifier.size(14.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${candidate.location} • ${candidate.foundDate}",
                    fontSize = 12.sp,
                    color = Color(0xFF475569),
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = candidate.matchReason,
                    fontSize = 11.sp,
                    color = Color(0xFF475569),
                )

                TextButton(onClick = { onViewCandidate(candidate.id) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "View details",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D4ED8),
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color(0xFF1D4ED8),
                            modifier = Modifier.size(12.dp),
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Possible Matches View")
@Composable
fun PossibleMatchesPreview() {
    App_1Theme {
        PossibleMatchesScreen(onBack = {}, hasMatches = true)
    }
}

@Preview(showBackground = true, name = "No Matches Yet View")
@Composable
fun NoMatchesYetPreview() {
    App_1Theme {
        PossibleMatchesScreen(onBack = {}, hasMatches = false)
    }
}
