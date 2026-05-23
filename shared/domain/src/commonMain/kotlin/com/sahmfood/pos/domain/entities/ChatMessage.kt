package com.sahmfood.pos.domain.entities

enum class ChatRole { USER, ASSISTANT }

data class ChatMessage(
    val id: String,
    val role: ChatRole,
    val content: String,
    val timestampMs: Long,
)
