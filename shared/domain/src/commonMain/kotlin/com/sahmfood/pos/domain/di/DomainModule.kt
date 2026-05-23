package com.sahmfood.pos.domain.di

import com.sahmfood.pos.domain.usecases.AddItemToCart
import com.sahmfood.pos.domain.usecases.CalculateOrderTotals
import com.sahmfood.pos.domain.usecases.CheckoutOrder
import com.sahmfood.pos.domain.usecases.GetOrderDetails
import com.sahmfood.pos.domain.usecases.GetOrderHistory
import com.sahmfood.pos.domain.usecases.GetProductCatalog
import com.sahmfood.pos.domain.usecases.PrintReceipt
import com.sahmfood.pos.domain.usecases.RemoveItemFromCart
import com.sahmfood.pos.domain.usecases.UpdateItemQuantity
import org.koin.dsl.module

val domainModule = module {
    factory { AddItemToCart() }
    factory { RemoveItemFromCart() }
    factory { UpdateItemQuantity() }
    factory { CalculateOrderTotals() }
    factory { GetProductCatalog(get()) }
    factory { GetOrderHistory(get()) }
    factory { GetOrderDetails(get()) }
    factory { CheckoutOrder(get(), get(), get(), get()) }
    factory { PrintReceipt(get(), get(), get()) }
}
