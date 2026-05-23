package com.sahmfood.pos.data.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

actual class DatabaseFactory(private val context: Context) {
    actual fun builder(): RoomDatabase.Builder<SahmDatabase> {
        val dbFile = context.getDatabasePath(SAHM_DATABASE_NAME)
        return Room.databaseBuilder<SahmDatabase>(
            context = context.applicationContext,
            name = dbFile.absolutePath,
        )
    }
}
