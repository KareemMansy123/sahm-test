package com.sahmfood.pos.data.mappers

import com.sahmfood.pos.data.db.entities.ProductEntity
import com.sahmfood.pos.domain.entities.Money
import com.sahmfood.pos.domain.entities.Product

object ProductMapper {
    fun toDomain(entity: ProductEntity): Product = Product(
        id = entity.id,
        name = entity.name,
        price = Money(entity.priceAmount, entity.currency),
        category = entity.category,
        imageUrl = entity.imageUrl,
        description = entity.description,
        isAvailable = entity.isAvailable,
        nameAr = entity.nameAr,
        descriptionAr = entity.descriptionAr,
        categoryAr = entity.categoryAr,
    )

    fun toEntity(product: Product): ProductEntity = ProductEntity(
        id = product.id,
        name = product.name,
        priceAmount = product.price.amount,
        currency = product.price.currency,
        category = product.category,
        imageUrl = product.imageUrl,
        description = product.description,
        isAvailable = product.isAvailable,
        nameAr = product.nameAr,
        descriptionAr = product.descriptionAr,
        categoryAr = product.categoryAr,
    )

    fun toDomainList(entities: List<ProductEntity>): List<Product> = entities.map(::toDomain)
    fun toEntityList(products: List<Product>): List<ProductEntity> = products.map(::toEntity)
}
