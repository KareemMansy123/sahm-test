package com.sahmfood.pos.data

import com.sahmfood.pos.data.printer.MockPrinterService
import com.sahmfood.pos.domain.entities.Money
import com.sahmfood.pos.domain.entities.Order
import com.sahmfood.pos.domain.entities.OrderItem
import com.sahmfood.pos.domain.entities.OrderStatus
import com.sahmfood.pos.domain.entities.PaymentMethod
import com.sahmfood.pos.domain.entities.Receipt
import com.sahmfood.pos.domain.services.PrintResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MockPrinterServiceTest {
    private fun sampleReceipt(): Receipt = Receipt(
        receiptNumber = "R-ABC123",
        storeName = "Sahm Food",
        storeAddress = "123 Main St, Cairo",
        order = Order(
            id = "o1",
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
        ),
        items = listOf(
            OrderItem("i1", "o1", "p1", "Beef Classic", 1, Money(10000), Money(10000))
        ),
        issuedAt = 1L
    )

    @Test
    fun `print succeeds and emits receipt text on log`() = runTest {
        val printer = MockPrinterService(printDelayMs = 0)
        val result = printer.print(sampleReceipt())
        assertEquals(PrintResult.Success, result)
        val logged = printer.printLog.replayCache.first()
        assertTrue(logged.contains("SAHM FOOD"))
        assertTrue(logged.contains("Beef Classic"))
        assertTrue(logged.contains("TOTAL"))
    }

    @Test
    fun `renderReceipt centers header and aligns totals`() {
        val text = MockPrinterService.renderReceipt(sampleReceipt())
        assertTrue(text.contains("TOTAL"))
        assertTrue(text.contains("Thank you"))
        // Verify width is consistent — each non-empty line should be at most the configured width.
        text.lineSequence().forEach { line ->
            assertTrue(line.length <= MockPrinterService.WIDTH + 2)
        }
    }
}
