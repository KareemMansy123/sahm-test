package com.sahmfood.pos.presentation.checkout

import com.sahmfood.pos.domain.entities.Order
import com.sahmfood.pos.domain.entities.OrderItem
import com.sahmfood.pos.domain.repositories.OrderRepository
import com.sahmfood.pos.domain.services.PrintResult
import com.sahmfood.pos.domain.usecases.CheckoutOrder
import com.sahmfood.pos.domain.usecases.PrintReceipt
import com.sahmfood.pos.presentation.common.BaseStore

class CheckoutStore(
    private val checkoutOrder: CheckoutOrder,
    private val printReceipt: PrintReceipt,
    private val orderRepository: OrderRepository,
    private val renderReceiptText: (Order, List<OrderItem>) -> String,
) : BaseStore<CheckoutState, CheckoutIntent, CheckoutEffect>(CheckoutState()) {

    override suspend fun handle(intent: CheckoutIntent) {
        when (intent) {
            is CheckoutIntent.Initialize -> updateState {
                it.copy(items = intent.items, totals = intent.totals)
            }
            is CheckoutIntent.SetPaymentMethod -> updateState {
                it.copy(paymentMethod = intent.method)
            }
            CheckoutIntent.ConfirmPayment -> processPayment()
            CheckoutIntent.PrintReceipt -> printCompletedReceipt()
            CheckoutIntent.Done -> emitEffect(CheckoutEffect.NavigateBack)
        }
    }

    private suspend fun processPayment() {
        val current = state.value
        if (!current.canConfirm) {
            emitEffect(CheckoutEffect.ShowError("No items to charge"))
            return
        }
        updateState { it.copy(isProcessing = true, errorMessage = null) }
        try {
            // Tendered defaults to the grand total — no separate cashier
            // input for cash; the receipt shows zero change.
            val order = checkoutOrder(
                items = current.items,
                totals = current.totals,
                paymentMethod = current.paymentMethod,
                tendered = current.totals.grandTotal,
            )
            val items = orderRepository.getItems(order.id)
            updateState {
                it.copy(
                    isProcessing = false,
                    completedOrder = order,
                    completedItems = items,
                )
            }
            emitEffect(CheckoutEffect.PaymentSucceeded)
            printCompletedReceipt()
        } catch (t: Throwable) {
            updateState { it.copy(isProcessing = false, errorMessage = t.message) }
            emitEffect(CheckoutEffect.ShowError(t.message ?: "Payment failed"))
        }
    }

    private suspend fun printCompletedReceipt() {
        val order = state.value.completedOrder ?: return
        val items = state.value.completedItems
        when (val result = printReceipt(order, items)) {
            PrintResult.Success -> {
                val text = renderReceiptText(order, items)
                updateState { it.copy(printedReceiptText = text) }
                emitEffect(CheckoutEffect.ReceiptPrinted(text))
            }
            is PrintResult.Failure ->
                emitEffect(CheckoutEffect.ShowError("Print failed: ${result.reason}"))
        }
    }
}
