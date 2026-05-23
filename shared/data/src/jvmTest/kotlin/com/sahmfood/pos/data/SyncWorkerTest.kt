package com.sahmfood.pos.data

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.sahmfood.pos.data.db.SahmPosDatabase
import com.sahmfood.pos.data.repositories.OrderRepositoryImpl
import com.sahmfood.pos.data.repositories.SyncQueueRepositoryImpl
import com.sahmfood.pos.data.sync.AlwaysOfflineConnectivityObserver
import com.sahmfood.pos.data.sync.StubRemoteApiService
import com.sahmfood.pos.data.sync.SyncWorker
import com.sahmfood.pos.domain.entities.Money
import com.sahmfood.pos.domain.entities.Order
import com.sahmfood.pos.domain.entities.OrderStatus
import com.sahmfood.pos.domain.entities.PaymentMethod
import com.sahmfood.pos.domain.entities.SyncOpType
import com.sahmfood.pos.domain.entities.SyncQueueEntry
import com.sahmfood.pos.domain.entities.SyncStatus
import com.sahmfood.pos.domain.repositories.OrderRepository
import com.sahmfood.pos.domain.repositories.SyncQueueRepository
import com.sahmfood.pos.domain.services.AppClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class SyncWorkerTest {
    private class FixedClock(var now: Long = 1L) : AppClock {
        override fun nowMillis(): Long = now
    }

    private fun newWorld(): Triple<OrderRepository, SyncQueueRepository, SyncWorker> {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        SahmPosDatabase.Schema.create(driver)
        val db = SahmPosDatabase(driver)
        val clock = FixedClock()
        val orderRepo = OrderRepositoryImpl(db, clock)
        val syncRepo = SyncQueueRepositoryImpl(db)
        val remote = StubRemoteApiService(clock, latencyMs = 0)
        val conn = AlwaysOfflineConnectivityObserver()
        // Inject a test-controlled scope so processQueue runs deterministically
        // on the test thread instead of leaking onto a real Dispatchers.IO pool.
        val testScope = CoroutineScope(SupervisorJob() + UnconfinedTestDispatcher())
        val worker = SyncWorker(syncRepo, orderRepo, remote, conn, scope = testScope)
        return Triple(orderRepo, syncRepo, worker)
    }

    private fun sampleOrder(id: String): Order = Order(
        id = id,
        subtotal = Money(10000),
        tax = Money(1400),
        discount = Money.ZERO_EGP,
        grandTotal = Money(11400),
        status = OrderStatus.PAID,
        paymentMethod = PaymentMethod.CASH,
        tendered = Money(12000),
        change = Money(600),
        createdAt = 1L,
        updatedAt = 1L
    )

    @Test
    fun `triggerSync drains pending queue and marks orders synced`() = runTest {
        val (orderRepo, syncRepo, worker) = newWorld()
        val order = sampleOrder("o1")
        orderRepo.save(order, emptyList())
        syncRepo.enqueue(
            SyncQueueEntry(
                id = "s1",
                opType = SyncOpType.CREATE_ORDER,
                orderId = order.id,
                payloadJson = "{}",
                createdAt = 1L
            )
        )

        worker.triggerSync()

        assertEquals(0, syncRepo.getPending().size)
        assertEquals(OrderStatus.SYNCED, orderRepo.getById(order.id)?.status)
    }
}
