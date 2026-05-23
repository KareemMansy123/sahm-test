package com.sahmfood.pos.presentation.settings

import com.sahmfood.pos.domain.entities.AppThemePref
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
 * Reads/writes persisted preferences through use cases only. The store
 * keeps StateFlows so the UI can collect with the simple
 * `val theme by store.theme.collectAsState()` pattern.
 */
class AppSettingsStore(
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
            observePreferences().collect { prefs ->
                _theme.value = when (prefs.theme) {
                    AppThemePref.Light -> AppTheme.Light
                    AppThemePref.Dark -> AppTheme.Dark
                    AppThemePref.System -> AppTheme.System
                }
                _language.value = AppLanguage.fromCode(prefs.languageCode)
            }
        }
    }

    fun setTheme(theme: AppTheme) {
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
        scope.launch { updateLanguage(language.code) }
    }

    fun cancel() {
        // Settings store is a Koin single; cancellation happens at process death.
    }
}
