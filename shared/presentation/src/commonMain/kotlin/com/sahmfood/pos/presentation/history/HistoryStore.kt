package com.sahmfood.pos.presentation.history

import com.sahmfood.pos.domain.usecases.GetOrderDetails
import com.sahmfood.pos.domain.usecases.GetOrderHistory
import com.sahmfood.pos.presentation.common.BaseStore
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HistoryStore(
    private val getOrderHistory: GetOrderHistory,
    private val getOrderDetails: GetOrderDetails
) : BaseStore<HistoryState, HistoryIntent, HistoryEffect>(HistoryState()) {

    init {
        scope.launch {
            getOrderHistory()
                .catch { t ->
                    emitEffect(HistoryEffect.ShowError(t.message ?: "load failed"))
                }
                .collect { orders ->
                    updateState { it.copy(orders = orders, isLoading = false) }
                }
        }
    }

    override suspend fun handle(intent: HistoryIntent) {
        when (intent) {
            HistoryIntent.Load -> updateState { it.copy(isLoading = true) }
            is HistoryIntent.SelectOrder -> {
                val pair = getOrderDetails(intent.orderId)
                updateState {
                    it.copy(
                        selectedOrderId = intent.orderId,
                        selectedOrderItems = pair?.second ?: emptyList()
                    )
                }
            }
            HistoryIntent.ClearSelection -> updateState {
                it.copy(selectedOrderId = null, selectedOrderItems = emptyList())
            }
        }
    }
}
