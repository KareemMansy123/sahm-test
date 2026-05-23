package com.sahmfood.pos.data.sync

import com.sahmfood.pos.domain.sync.ConnectivityObserver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Default ConnectivityObserver — starts offline. Real Android impl would
 * back this with ConnectivityManager.NetworkCallback; iOS with NWPathMonitor.
 *
 * The MutableStateFlow is exposed via the [setOnline] helper for demos and
 * tests, letting us toggle connectivity from the UI to show the sync worker
 * draining the queue.
 */
class AlwaysOfflineConnectivityObserver : ConnectivityObserver {
    private val state = MutableStateFlow(false)
    override val isOnline: Flow<Boolean> = state.asStateFlow()

    fun setOnline(online: Boolean) {
        state.value = online
    }
}
