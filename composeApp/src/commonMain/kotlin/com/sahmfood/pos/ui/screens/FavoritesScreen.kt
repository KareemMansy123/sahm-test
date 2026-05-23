package com.sahmfood.pos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sahmfood.pos.domain.entities.Product
import com.sahmfood.pos.presentation.catalog.CatalogIntent
import com.sahmfood.pos.presentation.catalog.CatalogStore
import com.sahmfood.pos.presentation.favorites.FavoritesIntent
import com.sahmfood.pos.presentation.favorites.FavoritesStore
import com.sahmfood.pos.ui.components.PlazaEmptyState
import com.sahmfood.pos.ui.components.categoryIcon
import com.sahmfood.pos.ui.theme.BrandPrimary
import com.sahmfood.pos.ui.theme.Neutral60
import com.sahmfood.pos.ui.theme.Neutral95
import com.sahmfood.pos.ui.theme.SahmRadius
import com.sahmfood.pos.ui.theme.SahmSpacing
import com.sahmfood.pos.ui.theme.categoryGradient
import com.sahmfood.pos.ui.theme.plazaCardShadow

/**
 * Plaza-style favorites tab — list of horizontal cards (image left,
 * details + add-to-cart button center, heart button right).
 *
 * Tapping a card opens product detail (callback). Tapping the heart
 * removes the favorite (with implicit "undo via re-favorite" via the
 * heart icon on the catalog card).
 */
@Composable
fun FavoritesScreen(
    favoritesStore: FavoritesStore,
    catalogStore: CatalogStore,
    onOpenProduct: (Product) -> Unit,
    onBrowseMenu: () -> Unit,
) {
    val state by favoritesStore.state.collectAsState()
    when {
        state.isLoading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            androidx.compose.material3.CircularProgressIndicator(color = BrandPrimary)
        }
        state.favoriteProducts.isEmpty() -> PlazaEmptyState(
            icon = Icons.Rounded.FavoriteBorder,
            title = com.sahmfood.pos.ui.strings.LocalSahmStrings.current.favoritesEmptyTitle,
            description = com.sahmfood.pos.ui.strings.LocalSahmStrings.current.favoritesEmptyDescription,
            ctaLabel = com.sahmfood.pos.ui.strings.LocalSahmStrings.current.favoritesBrowseMenu,
            onCta = onBrowseMenu,
        )
        else -> LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(SahmSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(SahmSpacing.md),
        ) {
            items(state.favoriteProducts, key = { it.id }) { product ->
                FavoriteCard(
                    product = product,
                    onTap = { onOpenProduct(product) },
                    onAdd = { catalogStore.dispatch(CatalogIntent.AddToCart(product)) },
                    onRemove = { favoritesStore.dispatch(FavoritesIntent.Remove(product.id)) },
                )
            }
        }
    }
}

@Composable
private fun FavoriteCard(
    product: Product,
    onTap: () -> Unit,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .plazaCardShadow(shape = RoundedCornerShape(SahmRadius.md), elevation = 2.dp)
            .clickable(onClick = onTap),
        shape = RoundedCornerShape(SahmRadius.md),
        color = Color.White,
    ) {
        Row(
            modifier = Modifier.padding(SahmSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(SahmRadius.sm))
                    .background(Brush.linearGradient(categoryGradient(product.category))),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    categoryIcon(product.category),
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(36.dp),
                )
            }
            Spacer(Modifier.width(SahmSpacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    product.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                    ),
                    color = Neutral95,
                    maxLines = 1,
                )
                Spacer(Modifier.padding(top = 2.dp))
                Text(
                    product.category,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = Neutral60,
                )
                Spacer(Modifier.padding(top = 4.dp))
                Text(
                    product.price.toDisplayString(),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                    ),
                    color = BrandPrimary,
                )
            }
            // Quick add button
            Surface(
                modifier = Modifier
                    .size(40.dp)
                    .clickable(onClick = onAdd),
                shape = RoundedCornerShape(SahmRadius.sm),
                color = BrandPrimary,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Rounded.Add,
                        contentDescription = "Quick add ${product.name}",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
            IconButton(onClick = onRemove, modifier = Modifier.size(36.dp)) {
                Icon(
                    Icons.Rounded.Favorite,
                    contentDescription = "Remove favorite",
                    tint = BrandPrimary,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}
