package com.sahmfood.pos.data

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.sahmfood.pos.data.db.SahmPosDatabase
import com.sahmfood.pos.data.repositories.OrderRepositoryImpl
import com.sahmfood.pos.domain.entities.Money
import com.sahmfood.pos.domain.entities.Order
import com.sahmfood.pos.domain.entities.OrderItem
import com.sahmfood.pos.domain.entities.OrderStatus
import com.sahmfood.pos.domain.entities.PaymentMethod
import com.sahmfood.pos.domain.services.AppClock
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class OrderRepositoryImplTest {
    private fun newDb(): SahmPosDatabase {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        SahmPosDatabase.Schema.create(driver)
        return SahmPosDatabase(driver)
    }

    private class FixedClock(var now: Long = 1L) : AppClock {
        override fun nowMillis(): Long = now
    }

    private fun sampleOrder(id: String = "o1"): Order = Order(
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

    private fun sampleItems(orderId: String): List<OrderItem> = listOf(
        OrderItem(
            id = "i1",
            orderId = orderId,
            productId = "p1",
            productName = "Beef Classic",
            quantity = 1,
            unitPrice = Money(10000),
            lineTotal = Money(10000)
        )
    )

    @Test
    fun `saved order can be retrieved by id`() = runTest {
        val repo = OrderRepositoryImpl(newDb(), FixedClock())
        val order = sampleOrder()
        repo.save(order, sampleItems(order.id))
        val loaded = repo.getById(order.id)
        assertNotNull(loaded)
        assertEquals(order.id, loaded.id)
        assertEquals(11400, loaded.grandTotal.amount)
        assertEquals(OrderStatus.PAID, loaded.status)
    }

    @Test
    fun `getItems returns saved order items`() = runTest {
        val repo = OrderRepositoryImpl(newDb(), FixedClock())
        val order = sampleOrder()
        repo.save(order, sampleItems(order.id))
        val items = repo.getItems(order.id)
        assertEquals(1, items.size)
        assertEquals("Beef Classic", items.first().productName)
    }

    @Test
    fun `updateStatus mutates row`() = runTest {
        val clock = FixedClock(now = 1L)
        val repo = OrderRepositoryImpl(newDb(), clock)
        val order = sampleOrder()
        repo.save(order, sampleItems(order.id))
        clock.now = 2L
        repo.updateStatus(order.id, OrderStatus.SYNCED)
        val loaded = repo.getById(order.id)
        assertEquals(OrderStatus.SYNCED, loaded?.status)
        assertEquals(2L, loaded?.updatedAt)
    }

    @Test
    fun `missing order returns null`() = runTest {
        val repo = OrderRepositoryImpl(newDb(), FixedClock())
        assertNull(repo.getById("nope"))
    }
}
