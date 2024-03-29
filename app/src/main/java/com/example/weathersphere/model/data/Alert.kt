package com.example.weathersphere.model.data

data class Alert(
    val sender_name: String,
    val event: String,
    val start: Long,
    val end: Long,
    val description: String,
    val tags: List<String>
)
