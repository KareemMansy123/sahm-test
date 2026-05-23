package com.sahmfood.pos.presentation.checkout

import com.sahmfood.pos.presentation.common.Reducer

/**
 * Pure state transitions for the checkout flow. No I/O, no coroutines.
 * Every async branch (`ConfirmPayment`, `PrintReceipt`) leaves state
 * unchanged here — the [CheckoutMiddleware] picks them up and dispatches
 * follow-up intents that THIS reducer handles synchronously.
 */
object CheckoutReducer : Reducer<CheckoutState, CheckoutIntent> {
    override fun reduce(state: CheckoutState, intent: CheckoutIntent): CheckoutState =
        when (intent) {
            is CheckoutIntent.Initialize -> state.copy(
                items = intent.items,
                totals = intent.totals,
            )
            is CheckoutIntent.SetPaymentMethod -> state.copy(paymentMethod = intent.method)
            CheckoutIntent.ConfirmPayment ->
                if (state.canConfirm) state.copy(isProcessing = true, errorMessage = null)
                else state
            is CheckoutIntent.PaymentSucceeded -> state.copy(
                isProcessing = false,
                completedOrder = intent.order,
                completedItems = intent.items,
            )
            is CheckoutIntent.PaymentFailed -> state.copy(
                isProcessing = false,
                errorMessage = intent.message,
            )
            is CheckoutIntent.ReceiptRendered -> state.copy(printedReceiptText = intent.text)
            CheckoutIntent.PrintReceipt, CheckoutIntent.Done -> state
        }
}
