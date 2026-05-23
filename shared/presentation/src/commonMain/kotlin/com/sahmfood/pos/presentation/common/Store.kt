package com.sahmfood.pos.presentation.common

import com.sahmfood.pos.domain.services.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
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
 * MVI store contract.
 *
 * Two ways to implement:
 *  1. Extend [BaseStore] and override `handle(intent)` (legacy stores).
 *  2. Extend [ReducerStore], pass in a pure [Reducer] and a list of
 *     [Middleware]s (new, preferred — separates pure state logic from
 *     effectful logic and makes both testable in isolation).
 */
interface Store<S : Any, I : Any, E : Any> {
    val state: StateFlow<S>
    val effects: SharedFlow<E>
    fun dispatch(intent: I)
    fun cancel()
}

/**
 * Legacy base. New stores should prefer [ReducerStore].
 */
abstract class BaseStore<S : Any, I : Any, E : Any>(
    initialState: S,
    dispatchers: DispatcherProvider? = null,
    protected val scope: CoroutineScope =
        CoroutineScope(SupervisorJob() + (dispatchers?.default ?: kotlinx.coroutines.Dispatchers.Default)),
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

/**
 * Reducer + Middleware MVI store. Pass a pure [Reducer] for state
 * transitions and a list of [Middleware]s for side effects.
 *
 * Lifecycle:
 *   dispatch(intent)
 *     -> state := reducer.reduce(state, intent)
 *     -> for each middleware: middleware.process(scope, intent)
 *
 * Middlewares run sequentially in declaration order on the store's
 * coroutine scope.
 */
abstract class ReducerStore<S : Any, I : Any, E : Any>(
    initialState: S,
    private val reducer: Reducer<S, I>,
    private val middlewares: List<Middleware<S, I, E>> = emptyList(),
    dispatchers: DispatcherProvider? = null,
    protected val scope: CoroutineScope =
        CoroutineScope(SupervisorJob() + (dispatchers?.default ?: kotlinx.coroutines.Dispatchers.Default)),
) : Store<S, I, E> {

    private val _state = MutableStateFlow(initialState)
    private val _effects = MutableSharedFlow<E>(extraBufferCapacity = 16)

    override val state: StateFlow<S> = _state.asStateFlow()
    override val effects: SharedFlow<E> = _effects.asSharedFlow()

    private val middlewareScope = object : MiddlewareScope<S, I, E> {
        override val state: S get() = _state.value
        override fun updateState(reducer: (S) -> S) {
            _state.update(reducer)
        }
        override suspend fun emitEffect(effect: E) {
            _effects.emit(effect)
        }
        override fun dispatch(intent: I) {
            this@ReducerStore.dispatch(intent)
        }
    }

    override fun dispatch(intent: I) {
        // 1) Pure reducer first.
        _state.update { current -> reducer.reduce(current, intent) }
        // 2) Then middlewares (async side effects).
        scope.launch {
            for (middleware in middlewares) {
                middleware.process(middlewareScope, intent)
            }
        }
    }

    override fun cancel() {
        scope.cancel()
    }
}
