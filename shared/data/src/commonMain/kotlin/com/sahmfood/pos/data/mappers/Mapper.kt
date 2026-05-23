package com.sahmfood.pos.data.mappers

/**
 * Shared shape for one-way mappers. Each mapper is a stateless singleton
 * so it can be tested without DI and reused across repositories.
 *
 * Two-way mappers expose [toDomain] and [toEntity] explicitly rather
 * than implementing this interface twice.
 */
fun interface Mapper<In, Out> {
    fun map(input: In): Out
}

/** Convenience for mapping a list with a single-element mapper. */
fun <In, Out> Mapper<In, Out>.mapAll(list: List<In>): List<Out> = list.map(::map)
