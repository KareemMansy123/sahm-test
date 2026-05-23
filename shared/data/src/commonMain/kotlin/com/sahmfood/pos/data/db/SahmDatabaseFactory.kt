package com.sahmfood.pos.data.db

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers

/**
 * Single factory function used by Koin to build the [SahmDatabase].
 * Centralizes the SQLite driver + IO dispatcher choice so platforms only
 * have to supply a [RoomDatabase.Builder] with the correct file path.
 *
 * `fallbackToDestructiveMigration(true)` — if the on-disk schema does
 * not match the entity classes (e.g., a previous app version used
 * SQLDelight with a different layout), Room drops and recreates the
 * database instead of crashing. We accept the data loss because the
 * catalog is re-seeded on every launch and the only other tables that
 * matter (favorites / cart / chat / settings) are session-scoped.
 */
fun provideSahmDatabase(factory: DatabaseFactory): SahmDatabase =
    factory.builder()
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .fallbackToDestructiveMigration(dropAllTables = true)
        .build()

const val SAHM_DATABASE_NAME = "sahm_pos.db"
