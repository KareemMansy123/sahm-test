package com.sahmfood.pos.data.sync

import com.sahmfood.pos.domain.entities.SyncQueueEntry
import com.sahmfood.pos.domain.services.AppClock
import com.sahmfood.pos.domain.sync.RemoteApiService
import com.sahmfood.pos.domain.sync.SyncAck
import kotlinx.coroutines.delay

/**
 * Network stub. In a real deployment this would be a Ktor client posting to
 * the central Sahm Food backend. For the assignment, it simulates ~200ms
 * latency and acknowledges every push. Failure simulation can be wired by
 * passing a non-zero [failureRate].
 */
class StubRemoteApiService(
    private val clock: AppClock,
    private val latencyMs: Long = 200,
    private val failureRate: Double = 0.0
) : RemoteApiService {

    override suspend fun push(entry: SyncQueueEntry): SyncAck {
        delay(latencyMs)
        if (failureRate > 0 && kotlin.random.Random.nextDouble() < failureRate) {
            error("simulated network failure for ${entry.id}")
        }
        return SyncAck(
            serverTimestamp = clock.nowMillis(),
            serverOrderId = entry.orderId
        )
    }
}
