package com.sahmfood.pos.data.di

import com.sahmfood.pos.data.db.SahmPosDatabase
import com.sahmfood.pos.data.printer.MockPrinterService
import com.sahmfood.pos.data.repositories.InMemoryFavoritesRepository
import com.sahmfood.pos.data.repositories.OrderRepositoryImpl
import com.sahmfood.pos.data.repositories.ProductRepositoryImpl
import com.sahmfood.pos.data.repositories.SyncQueueRepositoryImpl
import com.sahmfood.pos.data.seed.CatalogSeed
import com.sahmfood.pos.data.services.RandomIdGenerator
import com.sahmfood.pos.data.services.SystemAppClock
import com.sahmfood.pos.data.sync.AlwaysOfflineConnectivityObserver
import com.sahmfood.pos.data.sync.StubRemoteApiService
import com.sahmfood.pos.data.sync.SyncWorker
import com.sahmfood.pos.domain.repositories.FavoritesRepository
import com.sahmfood.pos.domain.repositories.OrderRepository
import com.sahmfood.pos.domain.repositories.ProductRepository
import com.sahmfood.pos.domain.repositories.SyncQueueRepository
import com.sahmfood.pos.domain.services.AppClock
import com.sahmfood.pos.domain.services.IdGenerator
import com.sahmfood.pos.domain.services.PrinterService
import com.sahmfood.pos.domain.sync.ConnectivityObserver
import com.sahmfood.pos.domain.sync.RemoteApiService
import org.koin.core.module.Module
import org.koin.dsl.module

val dataModule: Module = module {
    // Database
    single { SahmPosDatabase(get<com.sahmfood.pos.data.db.DatabaseDriverFactory>().create()) }

    // Platform services
    single<AppClock> { SystemAppClock() }
    single<IdGenerator> { RandomIdGenerator() }

    // Repositories
    single<ProductRepository> { ProductRepositoryImpl(get()) }
    single<OrderRepository> { OrderRepositoryImpl(get(), get()) }
    single<SyncQueueRepository> { SyncQueueRepositoryImpl(get()) }
    single<FavoritesRepository> { InMemoryFavoritesRepository() }

    // Printer — exposed under both its concrete and interface type so the UI
    // can subscribe to MockPrinterService.printLog while use cases inject the
    // PrinterService interface.
    single { MockPrinterService() }
    single<PrinterService> { get<MockPrinterService>() }

    // Sync
    single { AlwaysOfflineConnectivityObserver() }
    single<ConnectivityObserver> { get<AlwaysOfflineConnectivityObserver>() }
    single<RemoteApiService> { StubRemoteApiService(get()) }
    single { SyncWorker(get(), get(), get(), get()) }

    // Seed
    single { CatalogSeed(get()) }
}
