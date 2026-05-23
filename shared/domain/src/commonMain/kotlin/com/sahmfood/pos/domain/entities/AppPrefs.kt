package com.sahmfood.pos.domain.entities

enum class AppThemePref { Light, Dark, System }

data class AppPreferences(
    val theme: AppThemePref,
    val languageCode: String,
) {
    companion object {
        val DEFAULT = AppPreferences(
            theme = AppThemePref.System,
            languageCode = "en",
        )
    }
}
