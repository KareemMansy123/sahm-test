package com.sahmfood.pos.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.sahmfood.pos.presentation.settings.AppLanguage
import com.sahmfood.pos.presentation.settings.AppSettingsStore
import com.sahmfood.pos.presentation.settings.AppTheme
import com.sahmfood.pos.ui.strings.LocalSahmStrings
import com.sahmfood.pos.ui.strings.stringsFor
import org.koin.compose.koinInject

@Composable
fun SahmTheme(
    settings: AppSettingsStore = koinInject(),
    content: @Composable () -> Unit,
) {
    val theme by settings.theme.collectAsState()
    val language by settings.language.collectAsState()
    val systemDark = isSystemInDarkTheme()
    val isDark = when (theme) {
        AppTheme.Light -> false
        AppTheme.Dark -> true
        AppTheme.System -> systemDark
    }
    val layoutDirection = when (language) {
        AppLanguage.English -> LayoutDirection.Ltr
        AppLanguage.Arabic -> LayoutDirection.Rtl
    }
    CompositionLocalProvider(
        LocalLayoutDirection provides layoutDirection,
        LocalSahmStrings provides stringsFor(language),
    ) {
        MaterialTheme(
            colorScheme = if (isDark) SahmDarkColors else SahmLightColors,
            typography = SahmTypography,
            content = content,
        )
    }
}
