package com.sahmfood.pos.domain.common

/**
 * Domain-meaningful error model. Use cases never throw — they return
 * `AppResult<T> = Result<T, AppError>` so callers handle every failure
 * mode as a value.
 *
 * Each variant carries a single human-readable [message] that the UI
 * layer can show directly, plus an optional [cause] for diagnostics.
 *
 * Add a new variant when a new failure category appears — never reach
 * for a generic Unknown unless the failure truly is unclassifiable.
 */
sealed class AppError(open val message: String, open val cause: Throwable? = null) {

    /** Persistence / database read/write failure. */
    data class Database(
        override val message: String,
        override val cause: Throwable? = null,
    ) : AppError(message, cause)

    /** Network / remote service failure (sync, downloads, etc.). */
    data class Network(
        override val message: String,
        override val cause: Throwable? = null,
    ) : AppError(message, cause)

    /** Caller-side validation: cart empty, insufficient tender, etc. */
    data class Validation(
        override val message: String,
        override val cause: Throwable? = null,
    ) : AppError(message, cause)

    /** Requested resource doesn't exist. */
    data class NotFound(
        override val message: String,
        override val cause: Throwable? = null,
    ) : AppError(message, cause)

    /** Hardware operation failed (printer offline, scanner not detected). */
    data class Hardware(
        override val message: String,
        override val cause: Throwable? = null,
    ) : AppError(message, cause)

    /** Catch-all for truly unexpected throwables. */
    data class Unknown(
        override val message: String,
        override val cause: Throwable? = null,
    ) : AppError(message, cause)

    companion object {
        /** Translate an arbitrary throwable into a sensible AppError. */
        fun from(t: Throwable, fallback: String = "Something went wrong"): AppError = when (t) {
            is IllegalArgumentException -> Validation(t.message ?: fallback, t)
            is IllegalStateException -> Validation(t.message ?: fallback, t)
            is NoSuchElementException -> NotFound(t.message ?: fallback, t)
            else -> Unknown(t.message ?: fallback, t)
        }
    }
}
