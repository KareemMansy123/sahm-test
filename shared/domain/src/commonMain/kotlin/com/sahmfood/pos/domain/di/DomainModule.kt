package com.sahmfood.pos.domain.di

import com.sahmfood.pos.domain.usecases.AddItemToCart
import com.sahmfood.pos.domain.usecases.AddProductToCart
import com.sahmfood.pos.domain.usecases.CalculateOrderTotals
import com.sahmfood.pos.domain.usecases.CheckoutOrder
import com.sahmfood.pos.domain.usecases.ClearCart
import com.sahmfood.pos.domain.usecases.ClearChatHistory
import com.sahmfood.pos.domain.usecases.CountPendingSyncOrders
import com.sahmfood.pos.domain.usecases.FindProductByName
import com.sahmfood.pos.domain.usecases.GetFavoriteProducts
import com.sahmfood.pos.domain.usecases.GetOrderDetails
import com.sahmfood.pos.domain.usecases.GetOrderHistory
import com.sahmfood.pos.domain.usecases.GetProductCatalog
import com.sahmfood.pos.domain.usecases.GetTodayRevenueSummary
import com.sahmfood.pos.domain.usecases.ObserveCart
import com.sahmfood.pos.domain.usecases.ObserveChatMessages
import com.sahmfood.pos.domain.usecases.ObserveFavoriteIds
import com.sahmfood.pos.domain.usecases.ObservePreferences
import com.sahmfood.pos.domain.usecases.PrintReceipt
import com.sahmfood.pos.domain.usecases.RankItemsByVolume
import com.sahmfood.pos.domain.usecases.RecommendProducts
import com.sahmfood.pos.domain.usecases.RemoveCartItem
import com.sahmfood.pos.domain.usecases.SearchCatalog
import com.sahmfood.pos.domain.usecases.RemoveItemFromCart
import com.sahmfood.pos.domain.usecases.SaveChatMessage
import com.sahmfood.pos.domain.usecases.SeedCatalogIfNeeded
import com.sahmfood.pos.domain.usecases.SetCartItemQuantity
import com.sahmfood.pos.domain.usecases.SnapshotCart
import com.sahmfood.pos.domain.usecases.ToggleFavorite
import com.sahmfood.pos.domain.usecases.UpdateItemQuantity
import com.sahmfood.pos.domain.usecases.UpdateLanguage
import com.sahmfood.pos.domain.usecases.UpdateTheme
import org.koin.dsl.module

val domainModule = module {
    // Pure (no deps)
    factory { AddItemToCart() }
    factory { RemoveItemFromCart() }
    factory { UpdateItemQuantity() }
    factory { CalculateOrderTotals() }

    // Catalog
    factory { GetProductCatalog(get()) }
    factory { SeedCatalogIfNeeded(get()) }

    // Cart (persisted)
    factory { ObserveCart(get(), get()) }
    factory { AddProductToCart(get()) }
    factory { SetCartItemQuantity(get()) }
    factory { RemoveCartItem(get()) }
    factory { ClearCart(get()) }
    factory { SnapshotCart(get()) }

    // Orders / checkout
    factory { GetOrderHistory(get()) }
    factory { GetOrderDetails(get()) }
    factory { CheckoutOrder(get(), get(), get(), get()) }
    factory { PrintReceipt(get(), get(), get()) }

    // Favorites
    factory { ObserveFavoriteIds(get()) }
    factory { ToggleFavorite(get()) }
    factory { GetFavoriteProducts(get(), get()) }

    // AI insights + catalog actions
    factory { GetTodayRevenueSummary(get()) }
    factory { CountPendingSyncOrders(get()) }
    factory { RankItemsByVolume(get()) }
    factory { SearchCatalog(get()) }
    factory { RecommendProducts(get(), get()) }
    factory { FindProductByName(get()) }

    // Chat persistence
    factory { ObserveChatMessages(get()) }
    factory { SaveChatMessage(get()) }
    factory { ClearChatHistory(get()) }

    // Preferences
    factory { ObservePreferences(get()) }
    factory { UpdateTheme(get()) }
    factory { UpdateLanguage(get()) }
}
