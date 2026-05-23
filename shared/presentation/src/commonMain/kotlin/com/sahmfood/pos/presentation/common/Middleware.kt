package com.sahmfood.pos.presentation.common

/**
 * Side-effect interceptor. Runs after the reducer updates state and
 * before subscribers see the new state.
 *
 * A middleware can:
 *  • emit effects through [MiddlewareScope.emitEffect]
 *  • dispatch follow-up intents through [MiddlewareScope.dispatch]
 *  • call use cases and update state via [MiddlewareScope.updateState]
 *
 * Each middleware is self-contained and unit-testable in isolation;
 * stores compose a list of them rather than one giant when() block.
 */
fun interface Middleware<S, I, E> {
    suspend fun process(scope: MiddlewareScope<S, I, E>, intent: I)
}

interface MiddlewareScope<S, I, E> {
    val state: S
    fun updateState(reducer: (S) -> S)
    suspend fun emitEffect(effect: E)
    fun dispatch(intent: I)
}
