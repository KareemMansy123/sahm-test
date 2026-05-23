package com.sahmfood.pos.domain.services

/**
 * Resolves [open question #3] — UUID generation across platforms.
 *
 * commonMain has no built-in UUID. We expose this as a domain service so
 * the data layer can provide a platform-appropriate implementation (UUID
 * on JVM/Android, NSUUID on iOS) without polluting the domain with
 * expect/actual declarations.
 */
interface IdGenerator {
    fun newId(): String
}
