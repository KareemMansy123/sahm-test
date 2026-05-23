package com.sahmfood.pos.data.db

import androidx.room.RoomDatabase

/**
 * Each platform builds the Room database with its own filesystem path.
 * The returned [RoomDatabase.Builder] gets the bundled SQLite driver +
 * IO dispatcher applied centrally in [provideSahmDatabase].
 */
expect class DatabaseFactory {
    fun builder(): RoomDatabase.Builder<SahmDatabase>
}
