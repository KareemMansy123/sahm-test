package com.sahmfood.pos.domain.entities

import kotlinx.serialization.Serializable

/**
 * A menu item. Stores English fields by default plus optional Arabic
 * translations. UI calls [localizedName] / [localizedDescription] /
 * [localizedCategory] with the active language code; falls back to
 * English when the Arabic field is missing.
 */
@Serializable
data class Product(
    val id: String,
    val name: String,
    val price: Money,
    val category: String,
    val imageUrl: String?,
    val isAvailable: Boolean = true,
    val description: String = "",
    val nameAr: String? = null,
    val descriptionAr: String? = null,
    val categoryAr: String? = null,
) {
    fun localizedName(languageCode: String): String =
        if (languageCode == "ar" && !nameAr.isNullOrBlank()) nameAr else name

    fun localizedDescription(languageCode: String): String =
        if (languageCode == "ar" && !descriptionAr.isNullOrBlank()) descriptionAr else description

    fun localizedCategory(languageCode: String): String =
        if (languageCode == "ar" && !categoryAr.isNullOrBlank()) categoryAr else category
}
