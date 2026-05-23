package com.sahmfood.pos.data.services

import com.sahmfood.pos.domain.services.AppClock
import com.sahmfood.pos.domain.services.IdGenerator
import kotlinx.datetime.Clock

/** Cross-platform clock based on kotlinx.datetime. */
class SystemAppClock : AppClock {
    override fun nowMillis(): Long = Clock.System.now().toEpochMilliseconds()
}

/**
 * Random-based 16-char hex ID generator. Resolves [open question #3] — we
 * use a kotlin.random.Random based generator rather than java.util.UUID or
 * NSUUID. This keeps the domain free of expect/actual and is sufficient for
 * an offline-first single-device POS where collision probability over a
 * device's lifetime is negligible (~2^128 space halved per second).
 *
 * If multi-device sync ever requires globally unique IDs, swap in a UUIDv4
 * implementation via Koin without touching domain code.
 */
class RandomIdGenerator : IdGenerator {
    override fun newId(): String {
        val chars = "0123456789abcdef"
        return buildString(32) {
            repeat(32) { append(chars.random()) }
        }
    }
}
