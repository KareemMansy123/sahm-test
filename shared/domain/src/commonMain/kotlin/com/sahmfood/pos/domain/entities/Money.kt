package com.sahmfood.pos.domain.entities

import kotlinx.serialization.Serializable

/**
 * Currency value object. Amount stored in minor units (piastres for EGP) to
 * avoid floating-point drift on price arithmetic.
 */
@Serializable
data class Money(
    val amount: Long,
    val currency: String = "EGP"
) {
    init {
        require(currency.isNotBlank()) { "currency must not be blank" }
    }

    operator fun plus(other: Money): Money {
        require(currency == other.currency) {
            "cannot add $currency to ${other.currency}"
        }
        return copy(amount = amount + other.amount)
    }

    operator fun minus(other: Money): Money {
        require(currency == other.currency) {
            "cannot subtract ${other.currency} from $currency"
        }
        return copy(amount = amount - other.amount)
    }

    operator fun times(factor: Int): Money = copy(amount = amount * factor)

    operator fun compareTo(other: Money): Int {
        require(currency == other.currency)
        return amount.compareTo(other.amount)
    }

    /**
     * Apply a percentage as basis points (14% = 1400 bps) with **half-up
     * rounding** via Long arithmetic. Negative amounts are also rounded
     * away from zero so signs behave intuitively (`-150.5 → -151`).
     */
    fun percent(bps: Int): Money {
        val product = amount * bps
        val half = if (product >= 0) 5000L else -5000L
        return copy(amount = (product + half) / 10_000)
    }

    fun toDisplayString(): String {
        val whole = amount / 100
        val cents = (amount % 100).let { if (it < 0) -it else it }
        val sign = if (amount < 0 && whole == 0L) "-" else ""
        return "$currency $sign$whole.${cents.toString().padStart(2, '0')}"
    }

    companion object {
        val ZERO_EGP = Money(0, "EGP")
        fun egp(majorUnits: Int): Money = Money(majorUnits.toLong() * 100, "EGP")
    }
}
