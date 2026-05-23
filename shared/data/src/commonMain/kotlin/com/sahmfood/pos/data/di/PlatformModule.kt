package com.sahmfood.pos.data.di

import org.koin.core.module.Module

/**
 * Each platform binds its [DatabaseFactory] here. Android needs a
 * Context, iOS doesn't — DI must split.
 */
expect val platformDataModule: Module
