package com.sahmfood.pos.data.mappers

import com.sahmfood.pos.data.db.entities.ChatMessageEntity
import com.sahmfood.pos.domain.entities.ChatMessage
import com.sahmfood.pos.domain.entities.ChatRole

object ChatMessageMapper {
    fun toDomain(entity: ChatMessageEntity): ChatMessage = ChatMessage(
        id = entity.id,
        role = ChatRole.valueOf(entity.role),
        content = entity.content,
        timestampMs = entity.timestampMs,
    )

    fun toEntity(message: ChatMessage): ChatMessageEntity = ChatMessageEntity(
        id = message.id,
        role = message.role.name,
        content = message.content,
        timestampMs = message.timestampMs,
    )

    fun toDomainList(entities: List<ChatMessageEntity>): List<ChatMessage> =
        entities.map(::toDomain)
}
