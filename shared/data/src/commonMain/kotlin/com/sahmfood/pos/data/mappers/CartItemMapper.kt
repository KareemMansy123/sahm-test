package com.sahmfood.pos.data.mappers

import com.sahmfood.pos.data.db.entities.CartItemEntity
import com.sahmfood.pos.domain.entities.PersistedCartLine

object CartItemMapper {
    fun toDomain(entity: CartItemEntity): PersistedCartLine = PersistedCartLine(
        productId = entity.productId,
        quantity = entity.quantity,
        addedAt = entity.addedAt,
    )

    fun toEntity(line: PersistedCartLine): CartItemEntity = CartItemEntity(
        productId = line.productId,
        quantity = line.quantity,
        addedAt = line.addedAt,
    )

    fun toDomainList(entities: List<CartItemEntity>): List<PersistedCartLine> =
        entities.map(::toDomain)
}
