package com.example.caloriecounterappkotlin

import java.io.Serializable

data class Foods(
    val id: Long,
    val label: String,
    val calories: Double,
    val description: String?,
    val category: Int
): Serializable {
}