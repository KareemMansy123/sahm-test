package com.sahmfood.pos.data.repositories

import com.sahmfood.pos.data.db.dao.ChatMessageDao
import com.sahmfood.pos.data.mappers.ChatMessageMapper
import com.sahmfood.pos.domain.entities.ChatMessage
import com.sahmfood.pos.domain.repositories.ChatMessageRepository
import com.sahmfood.pos.domain.services.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ChatMessageRepositoryImpl(
    private val dao: ChatMessageDao,
    private val dispatchers: DispatcherProvider,
) : ChatMessageRepository {

    override fun observe(): Flow<List<ChatMessage>> =
        dao.observeAll()
            .map(ChatMessageMapper::toDomainList)
            .flowOn(dispatchers.io)

    override suspend fun add(message: ChatMessage) = withContext(dispatchers.io) {
        dao.insert(ChatMessageMapper.toEntity(message))
    }

    override suspend fun clear() = withContext(dispatchers.io) {
        dao.clear()
    }
}
