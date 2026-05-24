package com.sahmfood.pos.data.services

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Platform IO dispatcher. `Dispatchers.IO` exists on JVM/Android but is
 * internal on Kotlin/Native (iOS), so we expose a tiny expect/actual
 * accessor and pick the right thing per target:
 *
 *  - androidMain → `Dispatchers.IO` (real thread pool tuned for blocking IO)
 *  - iosMain     → `Dispatchers.Default` (Native has no separate IO pool;
 *                  Default is a worker pool that's already fit for both
 *                  CPU and blocking work on Apple platforms)
 */
expect fun ioDispatcher(): CoroutineDispatcher
