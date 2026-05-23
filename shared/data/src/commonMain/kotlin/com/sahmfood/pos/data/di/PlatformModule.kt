package com.sahmfood.pos.data.di

import org.koin.core.module.Module

/**
 * Each platform contributes a module that knows how to build the
 * platform-specific DatabaseDriverFactory. Android needs a Context, iOS does
 * not, so we can't bind a single common factory — DI must split here.
 */
expect val platformDataModule: Module
