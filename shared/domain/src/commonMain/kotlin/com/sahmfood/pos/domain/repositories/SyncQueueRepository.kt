package com.sahmfood.pos.domain.repositories

import com.sahmfood.pos.domain.entities.SyncQueueEntry
import com.sahmfood.pos.domain.entities.SyncStatus

interface SyncQueueRepository {
    suspend fun enqueue(entry: SyncQueueEntry)
    suspend fun getPending(): List<SyncQueueEntry>
    suspend fun markStatus(entryId: String, status: SyncStatus, attempts: Int)
    suspend fun getAll(): List<SyncQueueEntry>
}
