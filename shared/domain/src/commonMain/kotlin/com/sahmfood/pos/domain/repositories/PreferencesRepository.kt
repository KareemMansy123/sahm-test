package com.sahmfood.pos.domain.repositories

import com.sahmfood.pos.domain.entities.AppPreferences
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    fun observe(): Flow<AppPreferences>
    suspend fun snapshot(): AppPreferences
    suspend fun save(preferences: AppPreferences)
}
