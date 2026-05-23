package com.sahmfood.pos.presentation.checkout

import com.sahmfood.pos.domain.entities.CartItem
import com.sahmfood.pos.domain.entities.Money
import com.sahmfood.pos.domain.entities.Order
import com.sahmfood.pos.domain.entities.OrderItem
import com.sahmfood.pos.domain.entities.OrderTotals
import com.sahmfood.pos.domain.entities.PaymentMethod

data class CheckoutState(
    val items: List<CartItem> = emptyList(),
    val totals: OrderTotals = OrderTotals.EMPTY,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val tendered: Money = Money.ZERO_EGP,
    val isProcessing: Boolean = false,
    val completedOrder: Order? = null,
    val completedItems: List<OrderItem> = emptyList(),
    val printedReceiptText: String? = null,
    val errorMessage: String? = null
) {
    val change: Money get() {
        return if (paymentMethod == PaymentMethod.CASH && tendered.amount >= totals.grandTotal.amount) {
            tendered - totals.grandTotal
        } else {
            Money.ZERO_EGP
        }
    }
    val canConfirm: Boolean get() = when (paymentMethod) {
        PaymentMethod.CASH -> tendered.amount >= totals.grandTotal.amount && totals.grandTotal.amount > 0
        PaymentMethod.CARD -> totals.grandTotal.amount > 0
    }
}

sealed interface CheckoutIntent {
    data class Initialize(val items: List<CartItem>, val totals: OrderTotals) : CheckoutIntent
    data class SetPaymentMethod(val method: PaymentMethod) : CheckoutIntent
    data class SetTendered(val amount: Money) : CheckoutIntent
    data object ConfirmPayment : CheckoutIntent
    data object PrintReceipt : CheckoutIntent
    data object Done : CheckoutIntent
}

sealed interface CheckoutEffect {
    data object PaymentSucceeded : CheckoutEffect
    data class ReceiptPrinted(val text: String) : CheckoutEffect
    data class ShowError(val message: String) : CheckoutEffect
    data object NavigateBack : CheckoutEffect
}
