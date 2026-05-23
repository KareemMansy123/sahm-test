package com.sahmfood.pos.domain.usecases

import com.sahmfood.pos.domain.entities.ChatMessage
import com.sahmfood.pos.domain.repositories.ChatMessageRepository
import kotlinx.coroutines.flow.Flow

class ObserveChatMessages(private val repo: ChatMessageRepository) {
    operator fun invoke(): Flow<List<ChatMessage>> = repo.observe()
}

class SaveChatMessage(private val repo: ChatMessageRepository) {
    suspend operator fun invoke(message: ChatMessage) {
        repo.add(message)
    }
}

class ClearChatHistory(private val repo: ChatMessageRepository) {
    suspend operator fun invoke() {
        repo.clear()
    }
}
