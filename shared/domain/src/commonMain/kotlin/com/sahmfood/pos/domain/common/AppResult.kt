package com.sahmfood.pos.domain.common

/**
 * Use-case return type. A `Success(value)` or a `Failure(error)`.
 *
 * Stores match on the value rather than wrap calls in try/catch. The
 * runCatching helper wraps legacy throwing APIs while we finish the
 * migration; new code never throws across the use-case boundary.
 */
sealed interface AppResult<out T> {
    data class Success<T>(val value: T) : AppResult<T>
    data class Failure(val error: AppError) : AppResult<Nothing>

    companion object {
        fun <T> success(value: T): AppResult<T> = Success(value)
        fun failure(error: AppError): AppResult<Nothing> = Failure(error)
        fun failure(message: String, cause: Throwable? = null): AppResult<Nothing> =
            Failure(AppError.Unknown(message, cause))
    }
}

inline val <T> AppResult<T>.isSuccess: Boolean get() = this is AppResult.Success
inline val <T> AppResult<T>.isFailure: Boolean get() = this is AppResult.Failure

inline fun <T> AppResult<T>.getOrNull(): T? = when (this) {
    is AppResult.Success -> value
    is AppResult.Failure -> null
}

inline fun <T> AppResult<T>.errorOrNull(): AppError? = when (this) {
    is AppResult.Success -> null
    is AppResult.Failure -> error
}

inline fun <T> AppResult<T>.getOrElse(fallback: (AppError) -> T): T = when (this) {
    is AppResult.Success -> value
    is AppResult.Failure -> fallback(error)
}

inline fun <T, R> AppResult<T>.map(transform: (T) -> R): AppResult<R> = when (this) {
    is AppResult.Success -> AppResult.Success(transform(value))
    is AppResult.Failure -> this
}

inline fun <T, R> AppResult<T>.flatMap(transform: (T) -> AppResult<R>): AppResult<R> = when (this) {
    is AppResult.Success -> transform(value)
    is AppResult.Failure -> this
}

inline fun <T> AppResult<T>.onSuccess(action: (T) -> Unit): AppResult<T> {
    if (this is AppResult.Success) action(value)
    return this
}

inline fun <T> AppResult<T>.onFailure(action: (AppError) -> Unit): AppResult<T> {
    if (this is AppResult.Failure) action(error)
    return this
}

inline fun <T> AppResult<T>.fold(
    onSuccess: (T) -> Unit,
    onFailure: (AppError) -> Unit,
) {
    when (this) {
        is AppResult.Success -> onSuccess(value)
        is AppResult.Failure -> onFailure(error)
    }
}

/**
 * Wraps a possibly-throwing suspend block in an [AppResult]. Use this
 * at the data layer boundary (repository -> use case) to convert legacy
 * APIs without leaking exceptions to presentation.
 */
inline fun <T> appResultOf(block: () -> T): AppResult<T> = try {
    AppResult.Success(block())
} catch (t: Throwable) {
    AppResult.Failure(AppError.from(t))
}
