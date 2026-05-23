package com.sahmfood.pos.presentation.checkout

import com.sahmfood.pos.domain.common.fold
import com.sahmfood.pos.domain.entities.Order
import com.sahmfood.pos.domain.entities.OrderItem
import com.sahmfood.pos.domain.repositories.OrderRepository
import com.sahmfood.pos.domain.usecases.CheckoutOrder
import com.sahmfood.pos.domain.usecases.PrintReceipt
import com.sahmfood.pos.presentation.common.Middleware
import com.sahmfood.pos.presentation.common.MiddlewareScope

/**
 * Handles ConfirmPayment: invokes the CheckoutOrder use case (which
 * returns AppResult), then dispatches a PaymentSucceeded or
 * PaymentFailed back into the store so the pure reducer can update
 * state. Auto-triggers print on success.
 */
class CheckoutPaymentMiddleware(
    private val checkoutOrder: CheckoutOrder,
    private val orderRepository: OrderRepository,
) : Middleware<CheckoutState, CheckoutIntent, CheckoutEffect> {

    override suspend fun process(
        scope: MiddlewareScope<CheckoutState, CheckoutIntent, CheckoutEffect>,
        intent: CheckoutIntent,
    ) {
        if (intent !is CheckoutIntent.ConfirmPayment) return
        val s = scope.state
        if (!s.canConfirm) {
            scope.emitEffect(CheckoutEffect.ShowError("No items to charge"))
            return
        }
        val result = checkoutOrder(
            items = s.items,
            totals = s.totals,
            paymentMethod = s.paymentMethod,
            tendered = s.totals.grandTotal,
        )
        result.fold(
            onSuccess = { order ->
                val items = orderRepository.getItems(order.id)
                scope.dispatch(CheckoutIntent.PaymentSucceeded(order, items))
                scope.emitEffect(CheckoutEffect.PaymentSucceeded)
                scope.dispatch(CheckoutIntent.PrintReceipt)
            },
            onFailure = { error ->
                scope.dispatch(CheckoutIntent.PaymentFailed(error.message))
                scope.emitEffect(CheckoutEffect.ShowError(error.message))
            },
        )
    }
}

/**
 * Handles PrintReceipt: invokes the printer use case and emits a
 * ReceiptRendered intent (for state) + a ReceiptPrinted effect (for UI).
 */
class CheckoutPrintMiddleware(
    private val printReceipt: PrintReceipt,
    private val renderReceiptText: (Order, List<OrderItem>) -> String,
) : Middleware<CheckoutState, CheckoutIntent, CheckoutEffect> {

    override suspend fun process(
        scope: MiddlewareScope<CheckoutState, CheckoutIntent, CheckoutEffect>,
        intent: CheckoutIntent,
    ) {
        if (intent !is CheckoutIntent.PrintReceipt) return
        val order = scope.state.completedOrder ?: return
        val items = scope.state.completedItems
        printReceipt(order, items).fold(
            onSuccess = {
                val text = renderReceiptText(order, items)
                scope.dispatch(CheckoutIntent.ReceiptRendered(text))
                scope.emitEffect(CheckoutEffect.ReceiptPrinted(text))
            },
            onFailure = { error ->
                scope.emitEffect(CheckoutEffect.ShowError("Print failed: ${error.message}"))
            },
        )
    }
}

/** Translates Done intent to a NavigateBack effect. */
class CheckoutNavigationMiddleware :
    Middleware<CheckoutState, CheckoutIntent, CheckoutEffect> {
    override suspend fun process(
        scope: MiddlewareScope<CheckoutState, CheckoutIntent, CheckoutEffect>,
        intent: CheckoutIntent,
    ) {
        if (intent is CheckoutIntent.Done) {
            scope.emitEffect(CheckoutEffect.NavigateBack)
        }
    }
}
