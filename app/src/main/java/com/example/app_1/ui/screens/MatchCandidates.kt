package com.example.app_1.ui.screens

/** Public fields only. Similarity is a ranking hint, never proof of ownership. */
data class CandidateUiModel(
    val id: String,
    val title: String,
    val foundDate: String,
    val location: String,
    val category: String,
    val description: String,
    val status: String,
    val matchReason: String,
    val reportedAt: String,
    val similarityScore: Int? = null,
)

val sampleCandidates = listOf(
    CandidateUiModel("navy-backpack", "Navy backpack", "Found 14 Oct 2026", "Library · study area",
        "Bags", "Navy backpack with a front zip pocket.", "Held by office",
        "Similar category, colour & date", "2026-10-14"),
    CandidateUiModel("dark-backpack", "Dark backpack", "Found 15 Oct 2026", "Student Centre",
        "Bags", "Dark backpack with two shoulder straps.", "Held by office",
        "Similar category & colour", "2026-10-15"),
)
