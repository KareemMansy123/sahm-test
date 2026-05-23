package com.sahmfood.pos.domain.services

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Indirection over the kotlinx-coroutines `Dispatchers` singleton so
 * tests can substitute deterministic dispatchers.
 *
 * Anything that owns a CoroutineScope or calls `withContext(...)` should
 * inject this rather than referencing `Dispatchers` directly.
 */
interface DispatcherProvider {
    /** CPU-bound work (state reduction, computation). */
    val default: CoroutineDispatcher

    /** Blocking I/O (database, network). */
    val io: CoroutineDispatcher

    /** UI thread / main thread on Android & iOS. */
    val main: CoroutineDispatcher

    /** Unconfined — for unit tests; rarely used in production. */
    val unconfined: CoroutineDispatcher
}
