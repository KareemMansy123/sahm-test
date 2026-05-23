package com.sahmfood.pos.presentation.settings

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * App-wide settings store. Lives outside the MVI base because every
 * composable in the tree reads from it via `koinInject()` — making it a
 * StateFlow holder rather than an intent-driven store keeps call sites
 * trivial (`val theme by settings.theme.collectAsState()`).
 *
 * Persistence: in-memory only. Settings reset on process death. A
 * DataStore-backed implementation would slot in here without changing
 * the call sites.
 */
enum class AppTheme { Light, Dark, System }

enum class AppLanguage(val code: String, val displayName: String, val isRtl: Boolean) {
    English("en", "English", isRtl = false),
    Arabic("ar", "العربية", isRtl = true),
}

class AppSettingsStore {
    private val _theme = MutableStateFlow(AppTheme.System)
    val theme: StateFlow<AppTheme> = _theme.asStateFlow()

    private val _language = MutableStateFlow(AppLanguage.English)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    fun setTheme(theme: AppTheme) {
        _theme.value = theme
    }

    fun setLanguage(language: AppLanguage) {
        _language.value = language
    }
}
