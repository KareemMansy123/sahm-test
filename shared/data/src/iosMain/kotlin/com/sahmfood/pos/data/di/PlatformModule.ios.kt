package com.sahmfood.pos.data.di

import com.sahmfood.pos.data.db.DatabaseDriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformDataModule: Module = module {
    single { DatabaseDriverFactory() }
}
