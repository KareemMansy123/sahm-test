package com.sahmfood.pos.domain.sync

import com.sahmfood.pos.domain.entities.SyncQueueEntry

data class SyncAck(val serverTimestamp: Long, val serverOrderId: String?)

interface RemoteApiService {
    suspend fun push(entry: SyncQueueEntry): SyncAck
}
