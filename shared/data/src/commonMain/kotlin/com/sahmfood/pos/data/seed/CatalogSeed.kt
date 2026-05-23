package com.sahmfood.pos.data.seed

import com.sahmfood.pos.domain.entities.Money
import com.sahmfood.pos.domain.entities.Product
import com.sahmfood.pos.domain.repositories.ProductRepository

/**
 * Pre-seeded Sahm Food menu. Idempotent — re-running on app start refreshes
 * the catalog without duplicating rows (upsert via SQLDelight INSERT OR REPLACE).
 */
class CatalogSeed(private val productRepository: ProductRepository) {

    suspend fun seedIfEmpty() {
        // Always upsert. SQLDelight handles dedupe by primary key.
        productRepository.upsertAll(MENU)
    }

    companion object {
        val MENU: List<Product> = listOf(
            // Burgers
            Product("B01", "Beef Classic", Money.egp(85), "Burgers", null,
                description = "Single beef patty, cheddar, pickles, house sauce"),
            Product("B02", "Double Stack", Money.egp(130), "Burgers", null,
                description = "Double patty, caramelized onion, special sauce"),
            Product("B03", "Crispy Chicken", Money.egp(95), "Burgers", null,
                description = "Fried chicken fillet, coleslaw, jalapeño mayo"),
            Product("B04", "Mushroom Swiss", Money.egp(110), "Burgers", null,
                description = "Beef patty, sautéed mushrooms, swiss cheese"),

            // Pizza
            Product("P01", "Margherita", Money.egp(120), "Pizza", null,
                description = "Tomato, fresh mozzarella, basil"),
            Product("P02", "Pepperoni", Money.egp(150), "Pizza", null,
                description = "Loaded pepperoni, mozzarella, oregano"),
            Product("P03", "BBQ Chicken", Money.egp(165), "Pizza", null,
                description = "BBQ sauce, grilled chicken, red onion"),

            // Drinks
            Product("D01", "Mango Juice", Money.egp(45), "Drinks", null,
                description = "Fresh mango, chilled"),
            Product("D02", "Cola Can", Money.egp(25), "Drinks", null,
                description = "330ml chilled"),
            Product("D03", "Fresh Lemonade", Money.egp(50), "Drinks", null,
                description = "Squeezed lemon, mint, sugar"),

            // Sides
            Product("S01", "French Fries", Money.egp(40), "Sides", null,
                description = "Crispy cut, seasoning salt"),
            Product("S02", "Onion Rings", Money.egp(55), "Sides", null,
                description = "Beer-battered, ranch dip"),
            Product("S03", "Coleslaw Cup", Money.egp(30), "Sides", null,
                description = "Creamy, light vinegar dressing"),

            // Desserts
            Product("X01", "Chocolate Brownie", Money.egp(65), "Desserts", null,
                description = "Warm, walnut, vanilla ice cream"),
            Product("X02", "Kunafa Slice", Money.egp(75), "Desserts", null,
                description = "Classic cheese kunafa, sugar syrup"),
        )
    }
}
