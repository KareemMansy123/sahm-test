package com.sahmfood.pos.domain

import com.sahmfood.pos.domain.common.AppError
import com.sahmfood.pos.domain.common.AppResult
import com.sahmfood.pos.domain.common.getOrNull
import com.sahmfood.pos.domain.entities.CartItem
import com.sahmfood.pos.domain.entities.Money
import com.sahmfood.pos.domain.entities.Order
import com.sahmfood.pos.domain.entities.OrderItem
import com.sahmfood.pos.domain.entities.OrderStatus
import com.sahmfood.pos.domain.entities.OrderTotals
import com.sahmfood.pos.domain.entities.PaymentMethod
import com.sahmfood.pos.domain.entities.Product
import com.sahmfood.pos.domain.entities.SyncQueueEntry
import com.sahmfood.pos.domain.entities.SyncStatus
import com.sahmfood.pos.domain.repositories.OrderRepository
import com.sahmfood.pos.domain.repositories.SyncQueueRepository
import com.sahmfood.pos.domain.services.AppClock
import com.sahmfood.pos.domain.services.IdGenerator
import com.sahmfood.pos.domain.usecases.CheckoutOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CheckoutOrderTest {
    private class FakeOrderRepository : OrderRepository {
        val saved = mutableListOf<Pair<Order, List<OrderItem>>>()
        override suspend fun save(order: Order, items: List<OrderItem>) {
            saved += order to items
        }
        override suspend fun updateStatus(orderId: String, status: OrderStatus) {}
        override fun observeHistory(): Flow<List<Order>> = flowOf(saved.map { it.first })
        override suspend fun snapshotHistory(): List<Order> = saved.map { it.first }
        override suspend fun getById(orderId: String): Order? =
            saved.firstOrNull { it.first.id == orderId }?.first
        override suspend fun getItems(orderId: String): List<OrderItem> =
            saved.firstOrNull { it.first.id == orderId }?.second ?: emptyList()
    }

    private class FakeSyncRepo : SyncQueueRepository {
        val enqueued = mutableListOf<SyncQueueEntry>()
        override suspend fun enqueue(entry: SyncQueueEntry) { enqueued += entry }
        override suspend fun getPending() = enqueued.toList()
        override suspend fun markStatus(entryId: String, status: SyncStatus, attempts: Int) {}
        override suspend fun getAll() = enqueued.toList()
    }

    private class SeqIdGen : IdGenerator {
        private var n = 0
        override fun newId(): String = "id-${++n}"
    }
    private class FixedClock : AppClock { override fun nowMillis() = 1000L }

    private fun product(id: String, price: Long) =
        Product(id, "p$id", Money(price), "Burgers", null)

    @Test
    fun `checkout returns Success and saves order plus enqueues sync`() = runTest {
        val orderRepo = FakeOrderRepository()
        val syncRepo = FakeSyncRepo()
        val useCase = CheckoutOrder(orderRepo, syncRepo, SeqIdGen(), FixedClock())

        val items = listOf(CartItem(product("a", 8500), 2))
        val totals = OrderTotals(
            subtotal = Money(17000),
            taxAmount = Money(2380),
            discount = Money.ZERO_EGP,
            grandTotal = Money(19380),
        )
        val result = useCase(items, totals, PaymentMethod.CASH, tendered = Money(20000))

        assertTrue(result is AppResult.Success)
        val order = assertNotNull(result.getOrNull())
        assertEquals(OrderStatus.PAID, order.status)
        assertEquals(620, order.change.amount)
        assertEquals(1, orderRepo.saved.size)
        assertEquals(1, syncRepo.enqueued.size)
        assertEquals(order.id, syncRepo.enqueued.first().orderId)
    }

    @Test
    fun `empty cart returns Validation failure`() = runTest {
        val useCase = CheckoutOrder(FakeOrderRepository(), FakeSyncRepo(), SeqIdGen(), FixedClock())
        val result = useCase(emptyList(), OrderTotals.EMPTY, PaymentMethod.CASH, Money.ZERO_EGP)
        assertTrue(result is AppResult.Failure)
        assertTrue(result.error is AppError.Validation)
    }

    @Test
    fun `cash tender below total returns Validation failure`() = runTest {
        val useCase = CheckoutOrder(FakeOrderRepository(), FakeSyncRepo(), SeqIdGen(), FixedClock())
        val items = listOf(CartItem(product("a", 10000), 1))
        val totals = OrderTotals(Money(10000), Money(1400), Money.ZERO_EGP, Money(11400))
        val result = useCase(items, totals, PaymentMethod.CASH, tendered = Money(10000))
        assertTrue(result is AppResult.Failure)
        assertTrue(result.error is AppError.Validation)
    }

    @Test
    fun `card payment succeeds with zero tender`() = runTest {
        val useCase = CheckoutOrder(FakeOrderRepository(), FakeSyncRepo(), SeqIdGen(), FixedClock())
        val items = listOf(CartItem(product("a", 10000), 1))
        val totals = OrderTotals(Money(10000), Money(1400), Money.ZERO_EGP, Money(11400))
        val result = useCase(items, totals, PaymentMethod.CARD, tendered = Money.ZERO_EGP)
        assertTrue(result is AppResult.Success)
        assertEquals(PaymentMethod.CARD, result.value.paymentMethod)
        assertEquals(0, result.value.change.amount)
    }
}
