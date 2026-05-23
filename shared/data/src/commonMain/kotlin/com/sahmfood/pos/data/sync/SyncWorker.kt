package com.sahmfood.pos.data.sync

import com.sahmfood.pos.domain.entities.OrderStatus
import com.sahmfood.pos.domain.entities.SyncQueueEntry
import com.sahmfood.pos.domain.entities.SyncStatus
import com.sahmfood.pos.domain.repositories.OrderRepository
import com.sahmfood.pos.domain.repositories.SyncQueueRepository
import com.sahmfood.pos.domain.sync.ConnectivityObserver
import com.sahmfood.pos.domain.sync.RemoteApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Outbox processor. Drains the SyncQueueRepository on app start, when
 * [triggerSync] is called, or whenever the [ConnectivityObserver] reports
 * the device coming online.
 *
 * Retry strategy: exponential backoff (1s, 2s, 4s, 8s, …) up to 30s cap,
 * with a hard limit of MAX_ATTEMPTS. Entries that exceed the limit are
 * marked FAILED and their associated orders are set to SYNC_FAILED.
 *
 * Conflict resolution: server authoritative. The remote API echoes back an
 * orderId and timestamp; the worker simply marks the entry SUCCEEDED. A
 * real backend that rejects a duplicate would still respond with an Ack
 * keyed to the existing order, so the client treats both cases identically.
 */
class SyncWorker(
    private val queueRepo: SyncQueueRepository,
    private val orderRepo: OrderRepository,
    private val remoteApi: RemoteApiService,
    private val connectivity: ConnectivityObserver,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) {
    private val _state = MutableStateFlow<State>(State.Idle)
    val state: StateFlow<State> = _state.asStateFlow()

    private val mutex = Mutex()
    private var watchJob: Job? = null

    sealed class State {
        data object Idle : State()
        data class Running(val pendingCount: Int) : State()
        data class LastRun(val processed: Int, val failed: Int, val finishedAt: Long) : State()
    }

    /**
     * Idempotent. Starting a worker that is already watching is a no-op so the
     * caller (App.LaunchedEffect) does not have to track lifecycle state.
     */
    fun start() {
        if (watchJob?.isActive == true) return
        watchJob = scope.launch {
            connectivity.isOnline
                .filter { it }
                .collect { processQueue() }
        }
        scope.launch { processQueue() }
    }

    /** Manually trigger a drain — useful for demo "Sync now" buttons. */
    suspend fun triggerSync() = processQueue()

    private suspend fun processQueue() = mutex.withLock {
        val pending = queueRepo.getPending()
        if (pending.isEmpty()) {
            _state.value = State.Idle
            return@withLock
        }
        _state.value = State.Running(pending.size)
        var processed = 0
        var failed = 0
        pending.forEach { entry ->
            if (processEntry(entry)) processed++ else failed++
        }
        _state.value = State.LastRun(processed, failed, finishedAt = nowMillisSafe())
    }

    private suspend fun processEntry(entry: SyncQueueEntry): Boolean {
        if (entry.attempts >= MAX_ATTEMPTS) {
            queueRepo.markStatus(entry.id, SyncStatus.FAILED, entry.attempts)
            orderRepo.updateStatus(entry.orderId, OrderStatus.SYNC_FAILED)
            return false
        }
        // Backoff before retrying — first attempt skips the delay.
        if (entry.attempts > 0) {
            val backoff = (INITIAL_BACKOFF_MS shl (entry.attempts - 1))
                .coerceAtMost(MAX_BACKOFF_MS)
            delay(backoff)
        }
        queueRepo.markStatus(entry.id, SyncStatus.IN_FLIGHT, entry.attempts)
        return try {
            remoteApi.push(entry)
            queueRepo.markStatus(entry.id, SyncStatus.SUCCEEDED, entry.attempts + 1)
            orderRepo.updateStatus(entry.orderId, OrderStatus.SYNCED)
            true
        } catch (t: Throwable) {
            queueRepo.markStatus(entry.id, SyncStatus.PENDING, entry.attempts + 1)
            false
        }
    }

    private fun nowMillisSafe(): Long =
        kotlinx.datetime.Clock.System.now().toEpochMilliseconds()

    companion object {
        const val MAX_ATTEMPTS = 5
        const val INITIAL_BACKOFF_MS = 1_000L
        const val MAX_BACKOFF_MS = 30_000L
    }
}
