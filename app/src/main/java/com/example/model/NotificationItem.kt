package com.example.model

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timestampMillis: Long,
    val isRead: Boolean = false,
    val type: String = "REQUEST_UPDATE"
)
