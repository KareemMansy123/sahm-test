package com.sahmfood.pos.data.services

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Kotlin/Native has no separate IO pool. `Dispatchers.Default` is the
 * built-in worker pool on Apple platforms and is the standard recommendation
 * for both CPU-bound and blocking work in KMP iOS code.
 */
actual fun ioDispatcher(): CoroutineDispatcher = Dispatchers.Default
