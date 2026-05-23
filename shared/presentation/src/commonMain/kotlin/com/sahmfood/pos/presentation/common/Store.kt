package com.sahmfood.pos.presentation.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * MVI store contract. State is the single source of truth, Intents are the
 * only way to mutate it, Effects are one-shot fire-and-forget signals to the
 * UI (navigate, toast, trigger print).
 */
interface Store<S : Any, I : Any, E : Any> {
    val state: StateFlow<S>
    val effects: SharedFlow<E>
    fun dispatch(intent: I)
    fun cancel()
}

/**
 * Reusable base for stores. Each store owns a SupervisorJob scope so a
 * single coroutine failure does not cancel siblings. The scope is cancelled
 * via [cancel], typically from the Android ViewModel.onCleared() or a
 * DisposableEffect's onDispose on iOS.
 */
abstract class BaseStore<S : Any, I : Any, E : Any>(
    initialState: S,
    protected val scope: CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
) : Store<S, I, E> {

    private val _state = MutableStateFlow(initialState)
    private val _effects = MutableSharedFlow<E>(extraBufferCapacity = 16)

    override val state: StateFlow<S> = _state.asStateFlow()
    override val effects: SharedFlow<E> = _effects.asSharedFlow()

    override fun dispatch(intent: I) {
        scope.launch { handle(intent) }
    }

    protected abstract suspend fun handle(intent: I)

    protected fun updateState(reducer: (S) -> S) {
        _state.update(reducer)
    }

    protected suspend fun emitEffect(effect: E) {
        _effects.emit(effect)
    }

    override fun cancel() {
        scope.cancel()
    }
}
