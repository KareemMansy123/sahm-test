package com.sahmfood.pos.data.repositories

import com.sahmfood.pos.data.db.dao.SyncQueueDao
import com.sahmfood.pos.domain.entities.SyncQueueEntry
import com.sahmfood.pos.domain.entities.SyncStatus
import com.sahmfood.pos.domain.repositories.SyncQueueRepository

class SyncQueueRepositoryImpl(private val dao: SyncQueueDao) : SyncQueueRepository {

    override suspend fun enqueue(entry: SyncQueueEntry) {
        dao.insert(entry.toEntity())
    }

    override suspend fun getPending(): List<SyncQueueEntry> =
        dao.getPending().map { it.toDomain() }

    override suspend fun markStatus(entryId: String, status: SyncStatus, attempts: Int) {
        dao.updateStatus(entryId, status.name, attempts)
    }

    override suspend fun getAll(): List<SyncQueueEntry> =
        dao.getAll().map { it.toDomain() }
}
