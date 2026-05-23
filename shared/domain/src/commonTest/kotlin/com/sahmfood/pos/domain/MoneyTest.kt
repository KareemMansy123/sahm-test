package com.sahmfood.pos.domain

import com.sahmfood.pos.domain.entities.Money
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class MoneyTest {
    @Test
    fun `addition combines amounts when currencies match`() {
        val result = Money(5000) + Money(2500)
        assertEquals(7500, result.amount)
        assertEquals("EGP", result.currency)
    }

    @Test
    fun `addition fails on mismatched currency`() {
        assertFailsWith<IllegalArgumentException> {
            Money(100, "EGP") + Money(100, "USD")
        }
    }

    @Test
    fun `subtraction yields negative when right is larger`() {
        val result = Money(1000) - Money(2500)
        assertEquals(-1500, result.amount)
    }

    @Test
    fun `multiplication scales amount by factor`() {
        assertEquals(15000, (Money(5000) * 3).amount)
    }

    @Test
    fun `percent applies basis points with half-up rounding`() {
        // 14% of 10000 piastres = 1400
        assertEquals(1400, Money(10000).percent(1400).amount)
        // 14% of 7500 = 1050, exact, no rounding needed
        assertEquals(1050, Money(7500).percent(1400).amount)
        // 14% of 8550 = 1197 (half-up of 1197)
        assertEquals(1197, Money(8550).percent(1400).amount)
    }

    @Test
    fun `display string renders currency and two decimal places`() {
        assertEquals("EGP 50.00", Money(5000).toDisplayString())
        assertEquals("EGP 50.50", Money(5050).toDisplayString())
        assertEquals("EGP 0.05", Money(5).toDisplayString())
    }

    @Test
    fun `compareTo orders by amount`() {
        assertTrue(Money(100) < Money(200))
        assertTrue(Money(500) > Money(300))
        assertEquals(0, Money(100).compareTo(Money(100)))
    }

    @Test
    fun `egp factory converts whole units to piastres`() {
        assertEquals(8500, Money.egp(85).amount)
    }
}
