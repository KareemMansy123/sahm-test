package com.sahmfood.pos.ui.strings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.sahmfood.pos.domain.entities.Product
import com.sahmfood.pos.presentation.settings.AppLanguage
import com.sahmfood.pos.presentation.settings.AppSettingsStore
import org.koin.compose.koinInject

/** The currently-active language code ("en" / "ar"). */
@Composable
fun currentLanguageCode(
    settings: AppSettingsStore = koinInject(),
): String {
    val language by settings.language.collectAsState()
    return language.code
}

/** Localized product name for the active language. */
@Composable
@ReadOnlyComposable
fun Product.displayName(languageCode: String): String = localizedName(languageCode)

@Composable
@ReadOnlyComposable
fun Product.displayCategory(languageCode: String): String = localizedCategory(languageCode)

@Composable
@ReadOnlyComposable
fun Product.displayDescription(languageCode: String): String = localizedDescription(languageCode)

/**
 * Locale-aware version of AppLanguage.fromCode for places that have a
 * raw code string (e.g. from the settings store snapshot).
 */
fun languageFromCode(code: String): AppLanguage = AppLanguage.fromCode(code)
