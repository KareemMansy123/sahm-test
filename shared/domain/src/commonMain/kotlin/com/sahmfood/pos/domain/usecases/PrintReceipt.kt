package com.sahmfood.pos.domain.usecases

import com.sahmfood.pos.domain.common.AppError
import com.sahmfood.pos.domain.common.AppResult
import com.sahmfood.pos.domain.common.appResultOf
import com.sahmfood.pos.domain.common.flatMap
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
    private val clock: AppClock,
) {
    suspend operator fun invoke(order: Order, items: List<OrderItem>): AppResult<Unit> {
        val receipt = Receipt(
            receiptNumber = "R-${idGenerator.newId().takeLast(6).uppercase()}",
            storeName = "Sahm Food",
            storeAddress = "123 Main St, Cairo",
            order = order,
            items = items,
            issuedAt = clock.nowMillis(),
        )
        return appResultOf { printer.print(receipt) }.flatMap { result ->
            when (result) {
                PrintResult.Success -> AppResult.success(Unit)
                is PrintResult.Failure -> AppResult.failure(AppError.Hardware(result.reason))
            }
        }
    }
}
