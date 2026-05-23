package com.sahmfood.pos.data.mappers

import com.sahmfood.pos.data.db.entities.SettingsEntity
import com.sahmfood.pos.domain.entities.AppPreferences
import com.sahmfood.pos.domain.entities.AppThemePref

object PreferencesMapper {
    fun toDomain(entity: SettingsEntity): AppPreferences = AppPreferences(
        theme = runCatching { AppThemePref.valueOf(entity.theme) }
            .getOrDefault(AppThemePref.System),
        languageCode = entity.languageCode,
    )

    fun toEntity(preferences: AppPreferences): SettingsEntity = SettingsEntity(
        id = 0L,
        theme = preferences.theme.name,
        languageCode = preferences.languageCode,
    )
}
