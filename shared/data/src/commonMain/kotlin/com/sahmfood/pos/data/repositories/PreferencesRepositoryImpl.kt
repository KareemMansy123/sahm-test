package com.sahmfood.pos.data.repositories

import com.sahmfood.pos.data.db.dao.SettingsDao
import com.sahmfood.pos.data.db.entities.SettingsEntity
import com.sahmfood.pos.domain.entities.AppPreferences
import com.sahmfood.pos.domain.entities.AppThemePref
import com.sahmfood.pos.domain.repositories.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferencesRepositoryImpl(private val dao: SettingsDao) : PreferencesRepository {

    override fun observe(): Flow<AppPreferences> =
        dao.observe().map { it?.toDomain() ?: AppPreferences.DEFAULT }

    override suspend fun snapshot(): AppPreferences =
        dao.snapshot()?.toDomain() ?: AppPreferences.DEFAULT

    override suspend fun save(preferences: AppPreferences) {
        dao.upsert(
            SettingsEntity(
                id = 0L,
                theme = preferences.theme.name,
                languageCode = preferences.languageCode,
            )
        )
    }

    private fun SettingsEntity.toDomain(): AppPreferences = AppPreferences(
        theme = runCatching { AppThemePref.valueOf(theme) }.getOrDefault(AppThemePref.System),
        languageCode = languageCode,
    )
}
