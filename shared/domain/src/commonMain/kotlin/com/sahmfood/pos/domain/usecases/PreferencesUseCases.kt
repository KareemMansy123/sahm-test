package com.sahmfood.pos.domain.usecases

import com.sahmfood.pos.domain.entities.AppPreferences
import com.sahmfood.pos.domain.entities.AppThemePref
import com.sahmfood.pos.domain.repositories.PreferencesRepository
import kotlinx.coroutines.flow.Flow

class ObservePreferences(private val repo: PreferencesRepository) {
    operator fun invoke(): Flow<AppPreferences> = repo.observe()
}

class UpdateTheme(private val repo: PreferencesRepository) {
    suspend operator fun invoke(theme: AppThemePref) {
        val current = repo.snapshot()
        repo.save(current.copy(theme = theme))
    }
}

class UpdateLanguage(private val repo: PreferencesRepository) {
    suspend operator fun invoke(languageCode: String) {
        val current = repo.snapshot()
        repo.save(current.copy(languageCode = languageCode))
    }
}
