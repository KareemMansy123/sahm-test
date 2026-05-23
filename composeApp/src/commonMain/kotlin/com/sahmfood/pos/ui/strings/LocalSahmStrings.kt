package com.sahmfood.pos.ui.strings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import com.sahmfood.pos.presentation.settings.AppLanguage

/**
 * CompositionLocal that gives every composable in the tree access to the
 * active [SahmStrings]. Populated at the root by [SahmTheme] from the
 * AppSettingsStore's current language.
 *
 * Usage in any composable:  val str = LocalSahmStrings.current
 *                            Text(str.cartTitle)
 */
val LocalSahmStrings = compositionLocalOf<SahmStrings> { EnglishStrings }

fun stringsFor(language: AppLanguage): SahmStrings = when (language) {
    AppLanguage.English -> EnglishStrings
    AppLanguage.Arabic -> ArabicStrings
}

@Composable
@ReadOnlyComposable
fun strings(): SahmStrings = LocalSahmStrings.current
