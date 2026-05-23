package com.sahmfood.pos.data.repositories

import com.sahmfood.pos.data.db.dao.ChatMessageDao
import com.sahmfood.pos.domain.entities.ChatMessage
import com.sahmfood.pos.domain.repositories.ChatMessageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChatMessageRepositoryImpl(private val dao: ChatMessageDao) : ChatMessageRepository {

    override fun observe(): Flow<List<ChatMessage>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun add(message: ChatMessage) {
        dao.insert(message.toEntity())
    }

    override suspend fun clear() {
        dao.clear()
    }
}
