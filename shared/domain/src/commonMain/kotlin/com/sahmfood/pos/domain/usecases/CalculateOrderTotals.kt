package com.sahmfood.pos.domain.usecases

import com.sahmfood.pos.domain.entities.CartItem
import com.sahmfood.pos.domain.entities.Money
import com.sahmfood.pos.domain.entities.OrderTotals

class CalculateOrderTotals {
    /**
     * Applies a flat 14% VAT (Egypt standard) on top of subtotal. Discount is
     * subtracted after tax. Rounding: half-up via [Money.percent].
     *
     * Resolves [open question #6] — banker's vs half-up. Half-up matches what
     * most POS receipts in Egypt show and avoids the "wrong by a piastre"
     * complaints from cashiers reconciling cash drawers.
     */
    operator fun invoke(items: List<CartItem>, discount: Money = Money.ZERO_EGP): OrderTotals {
        if (items.isEmpty()) return OrderTotals.EMPTY
        val subtotal = items
            .map { it.lineTotal }
            .reduce { acc, m -> acc + m }
        val tax = subtotal.percent(TAX_RATE_BPS)
        val grandTotal = (subtotal + tax) - discount
        return OrderTotals(
            subtotal = subtotal,
            taxAmount = tax,
            discount = discount,
            grandTotal = grandTotal
        )
    }

    companion object {
        const val TAX_RATE_BPS = 1400  // 14.00% in basis points (×100)
    }
}
