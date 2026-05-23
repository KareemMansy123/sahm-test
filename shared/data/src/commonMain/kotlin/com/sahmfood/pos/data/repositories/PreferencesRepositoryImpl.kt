package com.sahmfood.pos.data.repositories

import com.sahmfood.pos.data.db.dao.SettingsDao
import com.sahmfood.pos.data.mappers.PreferencesMapper
import com.sahmfood.pos.domain.entities.AppPreferences
import com.sahmfood.pos.domain.repositories.PreferencesRepository
import com.sahmfood.pos.domain.services.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PreferencesRepositoryImpl(
    private val dao: SettingsDao,
    private val dispatchers: DispatcherProvider,
) : PreferencesRepository {

    override fun observe(): Flow<AppPreferences> =
        dao.observe()
            .map { it?.let(PreferencesMapper::toDomain) ?: AppPreferences.DEFAULT }
            .flowOn(dispatchers.io)

    override suspend fun snapshot(): AppPreferences = withContext(dispatchers.io) {
        dao.snapshot()?.let(PreferencesMapper::toDomain) ?: AppPreferences.DEFAULT
    }

    override suspend fun save(preferences: AppPreferences) = withContext(dispatchers.io) {
        dao.upsert(PreferencesMapper.toEntity(preferences))
    }
}
