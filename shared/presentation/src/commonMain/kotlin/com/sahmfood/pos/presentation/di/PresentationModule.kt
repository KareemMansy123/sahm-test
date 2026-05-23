package com.sahmfood.pos.presentation.di

import com.sahmfood.pos.presentation.catalog.CatalogStore
import com.sahmfood.pos.presentation.checkout.CheckoutStore
import com.sahmfood.pos.presentation.common.renderReceiptText
import com.sahmfood.pos.presentation.history.HistoryStore
import org.koin.dsl.module

val presentationModule = module {
    factory { CatalogStore(get(), get(), get(), get(), get()) }
    factory {
        CheckoutStore(
            checkoutOrder = get(),
            printReceipt = get(),
            orderRepository = get(),
            renderReceiptText = { order, items -> renderReceiptText(order, items) }
        )
    }
    factory { HistoryStore(get(), get()) }
}
