package com.sahmfood.pos.data.mappers

import com.sahmfood.pos.data.db.entities.SyncQueueEntity
import com.sahmfood.pos.domain.entities.SyncOpType
import com.sahmfood.pos.domain.entities.SyncQueueEntry
import com.sahmfood.pos.domain.entities.SyncStatus

object SyncQueueMapper {
    fun toDomain(entity: SyncQueueEntity): SyncQueueEntry = SyncQueueEntry(
        id = entity.id,
        opType = SyncOpType.valueOf(entity.opType),
        orderId = entity.orderId,
        payloadJson = entity.payloadJson,
        attempts = entity.attempts,
        status = SyncStatus.valueOf(entity.status),
        createdAt = entity.createdAt,
    )

    fun toEntity(entry: SyncQueueEntry): SyncQueueEntity = SyncQueueEntity(
        id = entry.id,
        opType = entry.opType.name,
        orderId = entry.orderId,
        payloadJson = entry.payloadJson,
        attempts = entry.attempts,
        status = entry.status.name,
        createdAt = entry.createdAt,
    )

    fun toDomainList(entities: List<SyncQueueEntity>): List<SyncQueueEntry> = entities.map(::toDomain)
}
