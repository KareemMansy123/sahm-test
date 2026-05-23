package com.sahmfood.pos.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cake
import androidx.compose.material.icons.rounded.Fastfood
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.LocalDrink
import androidx.compose.material.icons.rounded.LocalPizza
import androidx.compose.material.icons.rounded.Tapas
import androidx.compose.ui.graphics.vector.ImageVector

fun categoryIcon(name: String?): ImageVector = when (name?.lowercase()) {
    null -> Icons.Rounded.GridView
    "burgers" -> Icons.Rounded.Fastfood
    "pizza" -> Icons.Rounded.LocalPizza
    "drinks" -> Icons.Rounded.LocalDrink
    "desserts" -> Icons.Rounded.Cake
    "sides" -> Icons.Rounded.Tapas
    else -> Icons.Rounded.Fastfood
}
