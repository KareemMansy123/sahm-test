package com.sahmfood.pos.data.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "sync_queue", indices = [Index("status")])
data class SyncQueueEntity(
    @PrimaryKey val id: String,
    val opType: String,
    val orderId: String,
    val payloadJson: String,
    val attempts: Int,
    val status: String,
    val createdAt: Long,
)
