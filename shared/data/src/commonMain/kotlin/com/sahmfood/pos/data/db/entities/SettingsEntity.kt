package com.sahmfood.pos.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Single-row settings table. Always uses id = 0L; the upsert helper
 * guarantees there's never more than one row.
 */
@Entity(tableName = "app_settings")
data class SettingsEntity(
    @PrimaryKey val id: Long = 0L,
    val theme: String,
    val languageCode: String,
)
