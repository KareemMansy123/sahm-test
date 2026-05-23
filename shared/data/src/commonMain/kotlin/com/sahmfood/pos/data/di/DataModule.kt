package com.sahmfood.pos.data.di

import com.sahmfood.pos.data.db.SahmDatabase
import com.sahmfood.pos.data.db.provideSahmDatabase
import com.sahmfood.pos.data.printer.MockPrinterService
import com.sahmfood.pos.data.repositories.CartRepositoryImpl
import com.sahmfood.pos.data.repositories.ChatMessageRepositoryImpl
import com.sahmfood.pos.data.repositories.FavoritesRepositoryImpl
import com.sahmfood.pos.data.repositories.OrderRepositoryImpl
import com.sahmfood.pos.data.repositories.PreferencesRepositoryImpl
import com.sahmfood.pos.data.repositories.ProductRepositoryImpl
import com.sahmfood.pos.data.repositories.SyncQueueRepositoryImpl
import com.sahmfood.pos.data.services.DefaultDispatchers
import com.sahmfood.pos.data.services.RandomIdGenerator
import com.sahmfood.pos.data.services.SystemAppClock
import com.sahmfood.pos.data.sync.AlwaysOfflineConnectivityObserver
import com.sahmfood.pos.data.sync.StubRemoteApiService
import com.sahmfood.pos.data.sync.SyncWorker
import com.sahmfood.pos.domain.repositories.CartRepository
import com.sahmfood.pos.domain.repositories.ChatMessageRepository
import com.sahmfood.pos.domain.repositories.FavoritesRepository
import com.sahmfood.pos.domain.repositories.OrderRepository
import com.sahmfood.pos.domain.repositories.PreferencesRepository
import com.sahmfood.pos.domain.repositories.ProductRepository
import com.sahmfood.pos.domain.repositories.SyncQueueRepository
import com.sahmfood.pos.domain.services.AppClock
import com.sahmfood.pos.domain.services.DispatcherProvider
import com.sahmfood.pos.domain.services.IdGenerator
import com.sahmfood.pos.domain.services.PrinterService
import com.sahmfood.pos.domain.sync.ConnectivityObserver
import com.sahmfood.pos.domain.sync.RemoteApiService
import org.koin.core.module.Module
import org.koin.dsl.module

val dataModule: Module = module {
    // Database (Room)
    single { provideSahmDatabase(get()) }
    single { get<SahmDatabase>().productDao() }
    single { get<SahmDatabase>().orderDao() }
    single { get<SahmDatabase>().syncQueueDao() }
    single { get<SahmDatabase>().favoriteDao() }
    single { get<SahmDatabase>().cartDao() }
    single { get<SahmDatabase>().chatMessageDao() }
    single { get<SahmDatabase>().settingsDao() }

    // Platform services
    single<AppClock> { SystemAppClock() }
    single<IdGenerator> { RandomIdGenerator() }
    single<DispatcherProvider> { DefaultDispatchers() }

    // Repositories (domain interfaces → Room-backed impls)
    single<ProductRepository> { ProductRepositoryImpl(get(), get()) }
    single<OrderRepository> { OrderRepositoryImpl(get(), get(), get()) }
    single<SyncQueueRepository> { SyncQueueRepositoryImpl(get(), get()) }
    single<FavoritesRepository> { FavoritesRepositoryImpl(get(), get(), get()) }
    single<CartRepository> { CartRepositoryImpl(get(), get(), get()) }
    single<ChatMessageRepository> { ChatMessageRepositoryImpl(get(), get()) }
    single<PreferencesRepository> { PreferencesRepositoryImpl(get(), get()) }

    // Printer
    single { MockPrinterService() }
    single<PrinterService> { get<MockPrinterService>() }

    // Sync
    single { AlwaysOfflineConnectivityObserver() }
    single<ConnectivityObserver> { get<AlwaysOfflineConnectivityObserver>() }
    single<RemoteApiService> { StubRemoteApiService(get()) }
    single { SyncWorker(get(), get(), get(), get()) }
}
