package com.sahmfood.pos.data.printer

import com.sahmfood.pos.domain.entities.Receipt
import com.sahmfood.pos.domain.services.PrintResult
import com.sahmfood.pos.domain.services.PrinterService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Mock thermal-printer driver. Renders the receipt as plain text and emits
 * it on [printLog] so the UI can show a print preview / log panel. In a
 * production app this would push ESC/POS bytes to a USB/Bluetooth printer.
 */
class MockPrinterService(
    private val printDelayMs: Long = 400
) : PrinterService {

    private val _printLog = MutableSharedFlow<String>(replay = 16, extraBufferCapacity = 32)
    val printLog: SharedFlow<String> = _printLog.asSharedFlow()

    override suspend fun print(receipt: Receipt): PrintResult {
        delay(printDelayMs)
        val rendered = renderReceipt(receipt)
        _printLog.emit(rendered)
        return PrintResult.Success
    }

    companion object {
        const val WIDTH = 32

        fun renderReceipt(receipt: Receipt): String = buildString {
            val w = WIDTH
            fun divider(ch: Char = '=') = append(ch.toString().repeat(w)).append('\n')

            // Header
            appendLine(receipt.storeName.uppercase().centered(w))
            appendLine(receipt.storeAddress.centered(w))
            appendLine("Receipt #${receipt.receiptNumber}".centered(w))
            divider('=')

            // Items
            receipt.items.forEach { item ->
                val name = if (item.productName.length > 18) {
                    item.productName.take(17) + "…"
                } else item.productName
                val qty = "x${item.quantity}"
                val total = item.lineTotal.toDisplayString()
                appendLine(formatTwoCol("$name $qty", total, w))
            }
            divider('-')

            // Totals
            appendLine(formatTwoCol("Subtotal", receipt.order.subtotal.toDisplayString(), w))
            appendLine(formatTwoCol("Tax (14%)", receipt.order.tax.toDisplayString(), w))
            if (receipt.order.discount.amount > 0) {
                appendLine(formatTwoCol("Discount", "-" + receipt.order.discount.toDisplayString(), w))
            }
            divider('-')
            appendLine(formatTwoCol("TOTAL", receipt.order.grandTotal.toDisplayString(), w))
            divider('=')

            // Tender
            appendLine(formatTwoCol("Method", receipt.order.paymentMethod.name, w))
            if (receipt.order.paymentMethod.name == "CASH") {
                appendLine(formatTwoCol("Tendered", receipt.order.tendered.toDisplayString(), w))
                appendLine(formatTwoCol("Change", receipt.order.change.toDisplayString(), w))
            }
            divider('=')

            // Footer
            appendLine(receipt.footerMessage.centered(w))
        }

        private fun String.centered(width: Int): String {
            if (length >= width) return take(width)
            val pad = (width - length) / 2
            return " ".repeat(pad) + this
        }

        private fun formatTwoCol(left: String, right: String, width: Int): String {
            val space = (width - left.length - right.length).coerceAtLeast(1)
            return left + " ".repeat(space) + right
        }
    }
}
