package com.example.model

data class DelhiLocality(
    val id: String,
    val name: String,
    val zone: String,
    val latitude: Double,
    val longitude: Double,
    val landmark: String = ""
)
