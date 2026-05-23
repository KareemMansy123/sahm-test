package com.sahmfood.pos.domain.entities

data class OrderTotals(
    val subtotal: Money,
    val taxAmount: Money,
    val discount: Money,
    val grandTotal: Money
) {
    companion object {
        val EMPTY = OrderTotals(
            subtotal = Money.ZERO_EGP,
            taxAmount = Money.ZERO_EGP,
            discount = Money.ZERO_EGP,
            grandTotal = Money.ZERO_EGP
        )
    }
}
