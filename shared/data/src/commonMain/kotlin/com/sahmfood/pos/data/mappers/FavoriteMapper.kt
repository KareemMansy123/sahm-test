package com.sahmfood.pos.data.mappers

import com.sahmfood.pos.data.db.entities.FavoriteEntity

object FavoriteMapper {
    fun toProductId(entity: FavoriteEntity): String = entity.productId
    fun toProductIds(entities: List<FavoriteEntity>): List<String> = entities.map(::toProductId)
    fun fromProductId(productId: String, addedAt: Long): FavoriteEntity =
        FavoriteEntity(productId = productId, addedAt = addedAt)
}
