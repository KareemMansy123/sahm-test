package com.sahmfood.pos.domain.usecases

import com.sahmfood.pos.domain.entities.Order
import com.sahmfood.pos.domain.entities.OrderItem
import com.sahmfood.pos.domain.entities.Receipt
import com.sahmfood.pos.domain.services.AppClock
import com.sahmfood.pos.domain.services.IdGenerator
import com.sahmfood.pos.domain.services.PrintResult
import com.sahmfood.pos.domain.services.PrinterService

class PrintReceipt(
    private val printer: PrinterService,
    private val idGenerator: IdGenerator,
    private val clock: AppClock
) {
    suspend operator fun invoke(order: Order, items: List<OrderItem>): PrintResult {
        val receipt = Receipt(
            receiptNumber = "R-${idGenerator.newId().takeLast(6).uppercase()}",
            storeName = "Sahm Food",
            storeAddress = "123 Main St, Cairo",
            order = order,
            items = items,
            issuedAt = clock.nowMillis()
        )
        return printer.print(receipt)
    }
}
