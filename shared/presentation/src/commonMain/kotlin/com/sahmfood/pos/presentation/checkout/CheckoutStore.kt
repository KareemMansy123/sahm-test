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
    private val renderReceiptText: (Order, List<OrderItem>) -> String
) : BaseStore<CheckoutState, CheckoutIntent, CheckoutEffect>(CheckoutState()) {

    override suspend fun handle(intent: CheckoutIntent) {
        when (intent) {
            is CheckoutIntent.Initialize -> updateState {
                it.copy(items = intent.items, totals = intent.totals)
            }
            is CheckoutIntent.SetPaymentMethod -> updateState {
                it.copy(paymentMethod = intent.method)
            }
            is CheckoutIntent.SetTendered -> updateState {
                it.copy(tendered = intent.amount)
            }
            CheckoutIntent.ConfirmPayment -> processPayment()
            CheckoutIntent.PrintReceipt -> printCompletedReceipt()
            CheckoutIntent.Done -> emitEffect(CheckoutEffect.NavigateBack)
        }
    }

    private suspend fun processPayment() {
        val current = state.value
        if (!current.canConfirm) {
            emitEffect(CheckoutEffect.ShowError("Insufficient tender"))
            return
        }
        updateState { it.copy(isProcessing = true, errorMessage = null) }
        try {
            val order = checkoutOrder(
                items = current.items,
                totals = current.totals,
                paymentMethod = current.paymentMethod,
                tendered = current.tendered
            )
            val items = orderRepository.getItems(order.id)
            updateState {
                it.copy(
                    isProcessing = false,
                    completedOrder = order,
                    completedItems = items
                )
            }
            emitEffect(CheckoutEffect.PaymentSucceeded)
            // Auto-print on success.
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
            is PrintResult.Failure -> emitEffect(CheckoutEffect.ShowError("Print failed: ${result.reason}"))
        }
    }
}
