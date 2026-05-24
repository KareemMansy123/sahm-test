package com.sahmfood.pos.data.services

import com.sahmfood.pos.domain.services.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Production [DispatcherProvider]. Backed by the real kotlinx-coroutines
 * dispatchers. Tests substitute their own implementation backed by
 * `UnconfinedTestDispatcher` / `StandardTestDispatcher`.
 */
class DefaultDispatchers : DispatcherProvider {
    override val default: CoroutineDispatcher = Dispatchers.Default
    override val io: CoroutineDispatcher = ioDispatcher()
    override val main: CoroutineDispatcher = Dispatchers.Main
    override val unconfined: CoroutineDispatcher = Dispatchers.Unconfined
}
