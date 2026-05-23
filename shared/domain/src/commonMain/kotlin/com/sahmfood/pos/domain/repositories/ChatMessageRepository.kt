package com.sahmfood.pos.domain.repositories

import com.sahmfood.pos.domain.entities.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatMessageRepository {
    fun observe(): Flow<List<ChatMessage>>
    suspend fun add(message: ChatMessage)
    suspend fun clear()
}
