package com.sahmfood.pos.presentation.checkout

import com.sahmfood.pos.domain.entities.Order
import com.sahmfood.pos.domain.entities.OrderItem
import com.sahmfood.pos.domain.repositories.OrderRepository
import com.sahmfood.pos.domain.services.DispatcherProvider
import com.sahmfood.pos.domain.usecases.CheckoutOrder
import com.sahmfood.pos.domain.usecases.PrintReceipt
import com.sahmfood.pos.presentation.common.ReducerStore

/**
 * Refactored to use the Reducer + Middleware pattern.
 *
 * - [CheckoutReducer] handles all pure state transitions.
 * - [CheckoutPaymentMiddleware] handles ConfirmPayment (calls
 *   CheckoutOrder use case, dispatches PaymentSucceeded/Failed back to
 *   the reducer, emits effects).
 * - [CheckoutPrintMiddleware] handles PrintReceipt (calls PrintReceipt
 *   use case, dispatches ReceiptRendered back to the reducer).
 * - [CheckoutNavigationMiddleware] turns Done into a NavigateBack effect.
 *
 * No try/catch in this file. No `when` over intents. The store is just
 * wiring — the reducer is pure, each middleware is single-purpose.
 */
class CheckoutStore(
    checkoutOrder: CheckoutOrder,
    printReceipt: PrintReceipt,
    orderRepository: OrderRepository,
    renderReceiptText: (Order, List<OrderItem>) -> String,
    dispatchers: DispatcherProvider,
) : ReducerStore<CheckoutState, CheckoutIntent, CheckoutEffect>(
    initialState = CheckoutState(),
    reducer = CheckoutReducer,
    middlewares = listOf(
        CheckoutPaymentMiddleware(checkoutOrder, orderRepository),
        CheckoutPrintMiddleware(printReceipt, renderReceiptText),
        CheckoutNavigationMiddleware(),
    ),
    dispatchers = dispatchers,
)
