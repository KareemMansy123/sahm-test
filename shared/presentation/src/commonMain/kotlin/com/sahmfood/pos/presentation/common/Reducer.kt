package com.sahmfood.pos.presentation.common

/**
 * A pure (State, Intent) -> State function. No side effects, no
 * coroutines. Trivially testable: `assertEquals(expected, reducer(prev, intent))`.
 */
fun interface Reducer<S, I> {
    fun reduce(state: S, intent: I): S
}
