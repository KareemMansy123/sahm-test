package com.sahmfood.pos.presentation.settings

import com.sahmfood.pos.domain.entities.AppPreferences
import com.sahmfood.pos.domain.entities.AppThemePref
import com.sahmfood.pos.domain.repositories.PreferencesRepository
import com.sahmfood.pos.domain.usecases.ObservePreferences
import com.sahmfood.pos.domain.usecases.UpdateLanguage
import com.sahmfood.pos.domain.usecases.UpdateTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AppTheme { Light, Dark, System }

enum class AppLanguage(val code: String, val displayName: String, val isRtl: Boolean) {
    English("en", "English", isRtl = false),
    Arabic("ar", "العربية", isRtl = true);

    companion object {
        fun fromCode(code: String): AppLanguage =
            entries.firstOrNull { it.code == code } ?: English
    }
}

/**
 * Reads/writes persisted preferences through use cases. Holds a direct
 * reference to the repository as well so it can do a synchronous-first
 * `snapshot()` on init — without that, the StateFlow ships its `System`
 * / `English` defaults to the UI for ~1 frame before the observed flow
 * delivers the persisted value, causing the UI to flash from Dark→Light→Dark
 * on every cold launch.
 */
class AppSettingsStore(
    private val preferencesRepository: PreferencesRepository,
    private val observePreferences: ObservePreferences,
    private val updateTheme: UpdateTheme,
    private val updateLanguage: UpdateLanguage,
    private val scope: CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Default),
) {
    private val _theme = MutableStateFlow(AppTheme.System)
    val theme: StateFlow<AppTheme> = _theme.asStateFlow()

    private val _language = MutableStateFlow(AppLanguage.English)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    init {
        scope.launch {
            // Hydrate from snapshot first so the StateFlow already has the
            // persisted value before the first compose pass settles.
            applyPreferences(preferencesRepository.snapshot())
            // Then subscribe to live updates for the rest of the session.
            observePreferences().collect { applyPreferences(it) }
        }
    }

    private fun applyPreferences(prefs: AppPreferences) {
        _theme.value = when (prefs.theme) {
            AppThemePref.Light -> AppTheme.Light
            AppThemePref.Dark -> AppTheme.Dark
            AppThemePref.System -> AppTheme.System
        }
        _language.value = AppLanguage.fromCode(prefs.languageCode)
    }

    fun setTheme(theme: AppTheme) {
        // Optimistic update so the UI flips immediately, then persist.
        _theme.value = theme
        scope.launch {
            updateTheme(
                when (theme) {
                    AppTheme.Light -> AppThemePref.Light
                    AppTheme.Dark -> AppThemePref.Dark
                    AppTheme.System -> AppThemePref.System
                }
            )
        }
    }

    fun setLanguage(language: AppLanguage) {
        _language.value = language
        scope.launch { updateLanguage(language.code) }
    }

    fun cancel() {
        // AppSettingsStore is a Koin single; survives until process death.
    }
}
