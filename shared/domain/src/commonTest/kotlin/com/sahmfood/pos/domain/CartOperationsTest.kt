package com.sahmfood.pos.domain

import com.sahmfood.pos.domain.entities.CartItem
import com.sahmfood.pos.domain.entities.Money
import com.sahmfood.pos.domain.entities.Product
import com.sahmfood.pos.domain.usecases.AddItemToCart
import com.sahmfood.pos.domain.usecases.RemoveItemFromCart
import com.sahmfood.pos.domain.usecases.UpdateItemQuantity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CartOperationsTest {
    private val add = AddItemToCart()
    private val remove = RemoveItemFromCart()
    private val update = UpdateItemQuantity()

    private fun product(id: String) =
        Product(id = id, name = "p$id", price = Money(1000), category = "C", imageUrl = null)

    @Test
    fun `add new product appends to cart`() {
        val cart = add(emptyList(), product("a"))
        assertEquals(1, cart.size)
        assertEquals(1, cart.first().quantity)
    }

    @Test
    fun `add existing product increments quantity`() {
        val first = add(emptyList(), product("a"))
        val second = add(first, product("a"))
        assertEquals(1, second.size)
        assertEquals(2, second.first().quantity)
    }

    @Test
    fun `remove drops matching product`() {
        val cart = listOf(CartItem(product("a"), 1), CartItem(product("b"), 1))
        val after = remove(cart, "a")
        assertEquals(1, after.size)
        assertEquals("b", after.first().product.id)
    }

    @Test
    fun `update quantity to positive sets new quantity`() {
        val cart = listOf(CartItem(product("a"), 1))
        val after = update(cart, "a", 5)
        assertEquals(5, after.first().quantity)
    }

    @Test
    fun `update quantity to zero removes item`() {
        val cart = listOf(CartItem(product("a"), 3))
        val after = update(cart, "a", 0)
        assertTrue(after.isEmpty())
    }

    @Test
    fun `update non-existing product is no-op`() {
        val cart = listOf(CartItem(product("a"), 1))
        val after = update(cart, "z", 5)
        assertEquals(cart, after)
    }
}
