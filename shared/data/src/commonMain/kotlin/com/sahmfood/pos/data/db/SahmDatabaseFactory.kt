package com.sahmfood.pos.data.db

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers

/**
 * Single factory function used by Koin to build the [SahmDatabase].
 * Centralizes the SQLite driver + IO dispatcher choice so platforms only
 * have to supply a [RoomDatabase.Builder] with the correct file path.
 */
fun provideSahmDatabase(factory: DatabaseFactory): SahmDatabase =
    factory.builder()
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()

const val SAHM_DATABASE_NAME = "sahm_pos.db"
