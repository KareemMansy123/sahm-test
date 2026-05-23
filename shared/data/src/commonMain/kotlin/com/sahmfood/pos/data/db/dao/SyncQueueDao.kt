package com.sahmfood.pos.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sahmfood.pos.data.db.entities.SyncQueueEntity

@Dao
interface SyncQueueDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: SyncQueueEntity)

    @Query("SELECT * FROM sync_queue WHERE status = 'PENDING' ORDER BY createdAt ASC")
    suspend fun getPending(): List<SyncQueueEntity>

    @Query("SELECT * FROM sync_queue ORDER BY createdAt DESC")
    suspend fun getAll(): List<SyncQueueEntity>

    @Query("UPDATE sync_queue SET status = :status, attempts = :attempts WHERE id = :id")
    suspend fun updateStatus(id: String, status: String, attempts: Int)
}
