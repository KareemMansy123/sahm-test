package com.sahmfood.pos.domain.services

/**
 * Wraps the system clock so use cases can be unit-tested with a fake
 * implementation. Returns epoch milliseconds.
 */
interface AppClock {
    fun nowMillis(): Long
}
