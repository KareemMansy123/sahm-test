package com.sahmfood.pos.data.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.sahmfood.pos.data.db.dao.CartDao
import com.sahmfood.pos.data.db.dao.ChatMessageDao
import com.sahmfood.pos.data.db.dao.FavoriteDao
import com.sahmfood.pos.data.db.dao.OrderDao
import com.sahmfood.pos.data.db.dao.ProductDao
import com.sahmfood.pos.data.db.dao.SettingsDao
import com.sahmfood.pos.data.db.dao.SyncQueueDao
import com.sahmfood.pos.data.db.entities.CartItemEntity
import com.sahmfood.pos.data.db.entities.ChatMessageEntity
import com.sahmfood.pos.data.db.entities.FavoriteEntity
import com.sahmfood.pos.data.db.entities.OrderEntity
import com.sahmfood.pos.data.db.entities.OrderItemEntity
import com.sahmfood.pos.data.db.entities.ProductEntity
import com.sahmfood.pos.data.db.entities.SettingsEntity
import com.sahmfood.pos.data.db.entities.SyncQueueEntity

@Database(
    entities = [
        ProductEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        SyncQueueEntity::class,
        FavoriteEntity::class,
        CartItemEntity::class,
        ChatMessageEntity::class,
        SettingsEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@ConstructedBy(SahmDatabaseConstructor::class)
abstract class SahmDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao
    abstract fun syncQueueDao(): SyncQueueDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun cartDao(): CartDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun settingsDao(): SettingsDao
}

/** KSP-generated constructor implementation per Room 2.7 KMP contract. */
@Suppress("KotlinNoActualForExpect")
expect object SahmDatabaseConstructor : RoomDatabaseConstructor<SahmDatabase> {
    override fun initialize(): SahmDatabase
}
