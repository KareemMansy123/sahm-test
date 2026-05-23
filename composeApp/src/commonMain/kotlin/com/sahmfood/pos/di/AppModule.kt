package com.sahmfood.pos.di

import com.sahmfood.pos.data.di.dataModule
import com.sahmfood.pos.data.di.platformDataModule
import com.sahmfood.pos.domain.di.domainModule
import com.sahmfood.pos.presentation.di.presentationModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}): KoinApplication = startKoin {
    appDeclaration()
    modules(domainModule, dataModule, platformDataModule, presentationModule)
}
