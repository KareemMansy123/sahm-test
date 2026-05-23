package com.sahmfood.pos.domain

import com.sahmfood.pos.domain.entities.CartItem
import com.sahmfood.pos.domain.entities.Money
import com.sahmfood.pos.domain.entities.Product
import com.sahmfood.pos.domain.usecases.CalculateOrderTotals
import kotlin.test.Test
import kotlin.test.assertEquals

class CalculateOrderTotalsTest {
    private val useCase = CalculateOrderTotals()

    private fun product(id: String, price: Long) =
        Product(id = id, name = "p$id", price = Money(price), category = "Burgers", imageUrl = null)

    @Test
    fun `empty cart returns EMPTY totals`() {
        val totals = useCase(emptyList())
        assertEquals(0, totals.subtotal.amount)
        assertEquals(0, totals.taxAmount.amount)
        assertEquals(0, totals.grandTotal.amount)
    }

    @Test
    fun `single item applies 14 percent tax`() {
        val items = listOf(CartItem(product("a", 10000), quantity = 1))  // 100.00 EGP
        val totals = useCase(items)
        assertEquals(10000, totals.subtotal.amount)
        assertEquals(1400, totals.taxAmount.amount)
        assertEquals(11400, totals.grandTotal.amount)
    }

    @Test
    fun `multiple items sum line totals`() {
        val items = listOf(
            CartItem(product("a", 8500), 2),    // 170.00
            CartItem(product("b", 4500), 1)     // 45.00
        )
        val totals = useCase(items)
        assertEquals(21500, totals.subtotal.amount)        // 215.00
        assertEquals(3010, totals.taxAmount.amount)        // 30.10 (14%)
        assertEquals(24510, totals.grandTotal.amount)
    }

    @Test
    fun `discount is subtracted from subtotal plus tax`() {
        val items = listOf(CartItem(product("a", 10000), 1))
        val totals = useCase(items, discount = Money(500))
        assertEquals(11400 - 500, totals.grandTotal.amount)
    }
}
