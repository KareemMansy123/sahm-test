package com.sahmfood.pos.data.repositories

import com.sahmfood.pos.data.db.SahmPosDatabase
import com.sahmfood.pos.domain.entities.SyncQueueEntry
import com.sahmfood.pos.domain.entities.SyncStatus
import com.sahmfood.pos.domain.repositories.SyncQueueRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncQueueRepositoryImpl(
    private val db: SahmPosDatabase
) : SyncQueueRepository {

    override suspend fun enqueue(entry: SyncQueueEntry) = withContext(Dispatchers.IO) {
        db.sahmPosDatabaseQueries.insertSyncEntry(
            id = entry.id,
            op_type = entry.opType.name,
            order_id = entry.orderId,
            payload_json = entry.payloadJson,
            attempts = entry.attempts.toLong(),
            status = entry.status.name,
            created_at = entry.createdAt
        )
    }

    override suspend fun getPending(): List<SyncQueueEntry> = withContext(Dispatchers.IO) {
        db.sahmPosDatabaseQueries.selectPendingSync().executeAsList().map { it.toDomain() }
    }

    override suspend fun markStatus(entryId: String, status: SyncStatus, attempts: Int) =
        withContext(Dispatchers.IO) {
            db.sahmPosDatabaseQueries.updateSyncStatus(
                status = status.name,
                attempts = attempts.toLong(),
                id = entryId
            )
        }

    override suspend fun getAll(): List<SyncQueueEntry> = withContext(Dispatchers.IO) {
        db.sahmPosDatabaseQueries.selectAllSync().executeAsList().map { it.toDomain() }
    }
}
