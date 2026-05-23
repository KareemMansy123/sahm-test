package com.sahmfood.pos.data.repositories

import com.sahmfood.pos.data.db.dao.SyncQueueDao
import com.sahmfood.pos.data.mappers.SyncQueueMapper
import com.sahmfood.pos.domain.entities.SyncQueueEntry
import com.sahmfood.pos.domain.entities.SyncStatus
import com.sahmfood.pos.domain.repositories.SyncQueueRepository
import com.sahmfood.pos.domain.services.DispatcherProvider
import kotlinx.coroutines.withContext

class SyncQueueRepositoryImpl(
    private val dao: SyncQueueDao,
    private val dispatchers: DispatcherProvider,
) : SyncQueueRepository {

    override suspend fun enqueue(entry: SyncQueueEntry) = withContext(dispatchers.io) {
        dao.insert(SyncQueueMapper.toEntity(entry))
    }

    override suspend fun getPending(): List<SyncQueueEntry> = withContext(dispatchers.io) {
        SyncQueueMapper.toDomainList(dao.getPending())
    }

    override suspend fun markStatus(entryId: String, status: SyncStatus, attempts: Int) =
        withContext(dispatchers.io) {
            dao.updateStatus(entryId, status.name, attempts)
        }

    override suspend fun getAll(): List<SyncQueueEntry> = withContext(dispatchers.io) {
        SyncQueueMapper.toDomainList(dao.getAll())
    }
}
