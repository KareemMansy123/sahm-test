package com.sahmfood.pos.data.seed

import com.sahmfood.pos.domain.entities.Money
import com.sahmfood.pos.domain.entities.Product

/**
 * Static seed list of menu items. Owned by the data layer because the
 * fixture is data; the orchestration that writes it to the DB is the
 * domain layer's `SeedCatalogIfNeeded` use case.
 *
 * Each product carries both English and Arabic names + descriptions
 * so the catalog renders correctly when the user switches language.
 */
object MenuSeedData {
    private const val BURGERS_AR = "برجر"
    private const val PIZZA_AR = "بيتزا"
    private const val DRINKS_AR = "مشروبات"
    private const val SIDES_AR = "إضافات"
    private const val DESSERTS_AR = "حلويات"

    val MENU: List<Product> = listOf(
        // Burgers
        Product("B01", "Beef Classic", Money.egp(85), "Burgers", null,
            description = "Single beef patty, cheddar, pickles, house sauce",
            nameAr = "بيف كلاسيك",
            descriptionAr = "شريحة لحم بقري، جبن شيدر، مخلل، صلصة البيت",
            categoryAr = BURGERS_AR),
        Product("B02", "Double Stack", Money.egp(130), "Burgers", null,
            description = "Double patty, caramelized onion, special sauce",
            nameAr = "دبل ستاك",
            descriptionAr = "شريحتا لحم، بصل مكرمل، صلصة خاصة",
            categoryAr = BURGERS_AR),
        Product("B03", "Crispy Chicken", Money.egp(95), "Burgers", null,
            description = "Fried chicken fillet, coleslaw, jalapeño mayo",
            nameAr = "كرسبي تشيكن",
            descriptionAr = "فيليه دجاج مقلي، كولسلو، مايونيز هالابينو",
            categoryAr = BURGERS_AR),
        Product("B04", "Mushroom Swiss", Money.egp(110), "Burgers", null,
            description = "Beef patty, sautéed mushrooms, swiss cheese",
            nameAr = "ماشروم سويس",
            descriptionAr = "شريحة لحم بقري، مشروم سوتيه، جبن سويسري",
            categoryAr = BURGERS_AR),
        // Pizza
        Product("P01", "Margherita", Money.egp(120), "Pizza", null,
            description = "Tomato, fresh mozzarella, basil",
            nameAr = "مارجريتا",
            descriptionAr = "طماطم، موتزاريلا طازجة، ريحان",
            categoryAr = PIZZA_AR),
        Product("P02", "Pepperoni", Money.egp(150), "Pizza", null,
            description = "Loaded pepperoni, mozzarella, oregano",
            nameAr = "بيبروني",
            descriptionAr = "بيبروني وافر، موتزاريلا، أوريجانو",
            categoryAr = PIZZA_AR),
        Product("P03", "BBQ Chicken", Money.egp(165), "Pizza", null,
            description = "BBQ sauce, grilled chicken, red onion",
            nameAr = "دجاج باربكيو",
            descriptionAr = "صلصة باربكيو، دجاج مشوي، بصل أحمر",
            categoryAr = PIZZA_AR),
        // Drinks
        Product("D01", "Mango Juice", Money.egp(45), "Drinks", null,
            description = "Fresh mango, chilled",
            nameAr = "عصير مانجو",
            descriptionAr = "مانجو طازجة، باردة",
            categoryAr = DRINKS_AR),
        Product("D02", "Cola Can", Money.egp(25), "Drinks", null,
            description = "330ml chilled",
            nameAr = "كولا (علبة)",
            descriptionAr = "330 مل، باردة",
            categoryAr = DRINKS_AR),
        Product("D03", "Fresh Lemonade", Money.egp(50), "Drinks", null,
            description = "Squeezed lemon, mint, sugar",
            nameAr = "ليموناضة طازجة",
            descriptionAr = "ليمون معصور، نعناع، سكر",
            categoryAr = DRINKS_AR),
        // Sides
        Product("S01", "French Fries", Money.egp(40), "Sides", null,
            description = "Crispy cut, seasoning salt",
            nameAr = "بطاطس مقلية",
            descriptionAr = "مقطعة مقرمشة، ملح بهارات",
            categoryAr = SIDES_AR),
        Product("S02", "Onion Rings", Money.egp(55), "Sides", null,
            description = "Beer-battered, ranch dip",
            nameAr = "حلقات بصل",
            descriptionAr = "مقرمشة، صلصة رانش",
            categoryAr = SIDES_AR),
        Product("S03", "Coleslaw Cup", Money.egp(30), "Sides", null,
            description = "Creamy, light vinegar dressing",
            nameAr = "كولسلو",
            descriptionAr = "كريمي، صلصة خل خفيفة",
            categoryAr = SIDES_AR),
        // Desserts
        Product("X01", "Chocolate Brownie", Money.egp(65), "Desserts", null,
            description = "Warm, walnut, vanilla ice cream",
            nameAr = "براوني شوكولاتة",
            descriptionAr = "ساخن، عين جمل، آيس كريم فانيليا",
            categoryAr = DESSERTS_AR),
        Product("X02", "Kunafa Slice", Money.egp(75), "Desserts", null,
            description = "Classic cheese kunafa, sugar syrup",
            nameAr = "كنافة بالجبن",
            descriptionAr = "كنافة كلاسيكية بالجبن، شيرة سكر",
            categoryAr = DESSERTS_AR),
    )
}
