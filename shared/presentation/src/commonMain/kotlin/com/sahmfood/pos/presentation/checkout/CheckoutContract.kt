package com.sahmfood.pos.presentation.checkout

import com.sahmfood.pos.domain.entities.CartItem
import com.sahmfood.pos.domain.entities.Order
import com.sahmfood.pos.domain.entities.OrderItem
import com.sahmfood.pos.domain.entities.OrderTotals
import com.sahmfood.pos.domain.entities.PaymentMethod

data class CheckoutState(
    val items: List<CartItem> = emptyList(),
    val totals: OrderTotals = OrderTotals.EMPTY,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val isProcessing: Boolean = false,
    val completedOrder: Order? = null,
    val completedItems: List<OrderItem> = emptyList(),
    val printedReceiptText: String? = null,
    val errorMessage: String? = null,
) {
    val canConfirm: Boolean get() = totals.grandTotal.amount > 0
}

sealed interface CheckoutIntent {
    // User intents
    data class Initialize(val items: List<CartItem>, val totals: OrderTotals) : CheckoutIntent
    data class SetPaymentMethod(val method: PaymentMethod) : CheckoutIntent
    data object ConfirmPayment : CheckoutIntent
    data object PrintReceipt : CheckoutIntent
    data object Done : CheckoutIntent

    // Internal intents dispatched by middleware so the pure reducer can
    // own all state transitions.
    data class PaymentSucceeded(val order: Order, val items: List<OrderItem>) : CheckoutIntent
    data class PaymentFailed(val message: String) : CheckoutIntent
    data class ReceiptRendered(val text: String) : CheckoutIntent
}

sealed interface CheckoutEffect {
    data object PaymentSucceeded : CheckoutEffect
    data class ReceiptPrinted(val text: String) : CheckoutEffect
    data class ShowError(val message: String) : CheckoutEffect
    data object NavigateBack : CheckoutEffect
}
