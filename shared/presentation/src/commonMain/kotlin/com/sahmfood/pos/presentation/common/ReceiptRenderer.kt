package com.sahmfood.pos.presentation.common

import com.sahmfood.pos.domain.entities.Order
import com.sahmfood.pos.domain.entities.OrderItem

/**
 * Tiny presentation-layer renderer for the receipt text shown in the UI
 * preview. Mirrors what the mock printer "prints" so the on-screen receipt
 * and the printed log stay in sync.
 *
 * Kept in presentation because the UI binds to it directly; the data
 * layer's MockPrinterService has its own private rendering. They could
 * share a domain helper in a real product — for the assignment scope, two
 * tiny renderers is cleaner than another layer of indirection.
 */
fun renderReceiptText(order: Order, items: List<OrderItem>): String {
    val width = 32
    fun divider(c: Char) = c.toString().repeat(width)
    fun pad(left: String, right: String): String {
        val space = (width - left.length - right.length).coerceAtLeast(1)
        return left + " ".repeat(space) + right
    }
    fun center(s: String): String {
        if (s.length >= width) return s.take(width)
        val pad = (width - s.length) / 2
        return " ".repeat(pad) + s
    }
    return buildString {
        appendLine(center("SAHM FOOD"))
        appendLine(center("123 Main St, Cairo"))
        appendLine(center("Order ${order.id.takeLast(6).uppercase()}"))
        appendLine(divider('='))
        items.forEach { i ->
            val name = if (i.productName.length > 16) i.productName.take(15) + "…" else i.productName
            appendLine(pad("$name x${i.quantity}", i.lineTotal.toDisplayString()))
        }
        appendLine(divider('-'))
        appendLine(pad("Subtotal", order.subtotal.toDisplayString()))
        appendLine(pad("Tax (14%)", order.tax.toDisplayString()))
        if (order.discount.amount > 0) {
            appendLine(pad("Discount", "-" + order.discount.toDisplayString()))
        }
        appendLine(divider('-'))
        appendLine(pad("TOTAL", order.grandTotal.toDisplayString()))
        appendLine(divider('='))
        appendLine(pad("Method", order.paymentMethod.name))
        if (order.paymentMethod.name == "CASH") {
            appendLine(pad("Tendered", order.tendered.toDisplayString()))
            appendLine(pad("Change", order.change.toDisplayString()))
        }
        appendLine(divider('='))
        appendLine(center("Thank you! Come again."))
    }
}
