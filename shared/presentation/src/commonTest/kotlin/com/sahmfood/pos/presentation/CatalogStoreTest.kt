package com.sahmfood.pos.presentation

import app.cash.turbine.test
import com.sahmfood.pos.domain.entities.Money
import com.sahmfood.pos.domain.entities.Product
import com.sahmfood.pos.domain.repositories.ProductRepository
import com.sahmfood.pos.domain.usecases.AddItemToCart
import com.sahmfood.pos.domain.usecases.CalculateOrderTotals
import com.sahmfood.pos.domain.usecases.GetProductCatalog
import com.sahmfood.pos.domain.usecases.RemoveItemFromCart
import com.sahmfood.pos.domain.usecases.UpdateItemQuantity
import com.sahmfood.pos.presentation.catalog.CatalogEffect
import com.sahmfood.pos.presentation.catalog.CatalogIntent
import com.sahmfood.pos.presentation.catalog.CatalogStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CatalogStoreTest {
    private val sampleProducts = listOf(
        Product("a", "Beef", Money(8500), "Burgers", null),
        Product("b", "Margherita", Money(12000), "Pizza", null)
    )

    private class FakeProductRepository(private val products: List<Product>) : ProductRepository {
        override fun observeAll(): Flow<List<Product>> = flowOf(products)
        override suspend fun getById(id: String): Product? = products.firstOrNull { it.id == id }
        override suspend fun upsertAll(products: List<Product>) {}
    }

    private fun newStore(repo: ProductRepository = FakeProductRepository(sampleProducts)) =
        CatalogStore(
            getProductCatalog = GetProductCatalog(repo),
            addItemToCart = AddItemToCart(),
            removeItemFromCart = RemoveItemFromCart(),
            updateItemQuantity = UpdateItemQuantity(),
            calculateOrderTotals = CalculateOrderTotals()
        )

    @Test
    fun `initial load populates products and categories`() = runTest {
        val store = newStore()
        store.state.test {
            // Skip initial empty/loading state, await the loaded state.
            var loaded = awaitItem()
            while (loaded.products.isEmpty()) {
                loaded = awaitItem()
            }
            assertEquals(2, loaded.products.size)
            assertEquals(listOf("Burgers", "Pizza"), loaded.categories)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `add to cart updates cart and totals and emits effect`() = runTest {
        val store = newStore()
        // Subscribe to effects BEFORE dispatching so the assertion does not
        // rely on the SharedFlow buffer holding the emission.
        store.effects.test {
            // Wait for catalog to load before adding.
            store.state.test {
                var s = awaitItem()
                while (s.products.isEmpty()) { s = awaitItem() }
                cancelAndIgnoreRemainingEvents()
            }
            store.dispatch(CatalogIntent.AddToCart(sampleProducts.first()))
            val effect = awaitItem()
            assertTrue(effect is CatalogEffect.ProductAdded)
            cancelAndIgnoreRemainingEvents()
        }
        // Verify resulting state separately.
        val finalState = store.state.value
        assertEquals(1, finalState.cart.size)
        assertEquals(8500, finalState.totals.subtotal.amount)
        assertEquals(1190, finalState.totals.taxAmount.amount)   // 14% of 8500 = 1190
    }

    @Test
    fun `checkout on empty cart emits error`() = runTest {
        val store = newStore()
        store.effects.test {
            store.dispatch(CatalogIntent.Checkout)
            val effect = awaitItem()
            assertTrue(effect is CatalogEffect.ShowError)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `filter by category narrows products`() = runTest {
        val store = newStore()
        store.state.test {
            var s = awaitItem()
            while (s.products.isEmpty()) { s = awaitItem() }
            cancelAndIgnoreRemainingEvents()
        }
        store.dispatch(CatalogIntent.SelectCategory("Pizza"))
        store.state.test {
            val s = awaitItem()
            assertEquals("Pizza", s.selectedCategory)
            assertEquals(1, s.filteredProducts.size)
            assertEquals("b", s.filteredProducts.first().id)
        }
    }
}
