package com.sahmfood.pos.presentation.di

import com.sahmfood.pos.presentation.ai.AiChatStore
import com.sahmfood.pos.presentation.catalog.CatalogStore
import com.sahmfood.pos.presentation.checkout.CheckoutStore
import com.sahmfood.pos.presentation.common.renderReceiptText
import com.sahmfood.pos.presentation.favorites.FavoritesStore
import com.sahmfood.pos.presentation.history.HistoryStore
import com.sahmfood.pos.presentation.settings.AppSettingsStore
import org.koin.dsl.module

val presentationModule = module {
    factory {
        CatalogStore(
            getProductCatalog = get(),
            observeCart = get(),
            setCartItemQuantity = get(),
            removeCartItem = get(),
            clearCart = get(),
            snapshotCart = get(),
            calculateOrderTotals = get(),
        )
    }
    factory {
        CheckoutStore(
            checkoutOrder = get(),
            printReceipt = get(),
            orderRepository = get(),
            renderReceiptText = { order, items -> renderReceiptText(order, items) },
            dispatchers = get(),
        )
    }
    factory { HistoryStore(get(), get()) }

    // Singles — state shared across tabs / re-entry
    single { FavoritesStore(get(), get(), get()) }
    single {
        AiChatStore(
            observeChatMessages = get(),
            saveChatMessage = get(),
            clearChatHistory = get(),
            getTodayRevenueSummary = get(),
            countPendingSyncOrders = get(),
            rankItemsByVolume = get(),
            searchCatalog = get(),
            recommendProducts = get(),
            findProductByName = get(),
            addProductToCart = get(),
            snapshotCart = get(),
            clock = get(),
            ids = get(),
        )
    }
    single {
        AppSettingsStore(
            preferencesRepository = get(),
            observePreferences = get(),
            updateTheme = get(),
            updateLanguage = get(),
        )
    }
}
