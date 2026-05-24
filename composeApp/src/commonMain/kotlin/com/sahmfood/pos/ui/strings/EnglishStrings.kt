package com.sahmfood.pos.ui.strings

object EnglishStrings : SahmStrings {
    // Bottom nav
    override val navHome = "Home"
    override val navCart = "My Cart"
    override val navCategories = "Categories"
    override val navOrders = "Orders"
    override val navProfile = "Profile"

    // Home
    override val homeStoreName = "Sahm Food POS"
    override val homeCashierLabel = "Cashier"
    override val homeCashierValue = "Sahm Food · Counter 1"
    override val homeBannerTitle = "Today's Special"
    override val homeBannerSubtitle = "20% off combo meals · Express only"
    override val homeBannerCta = "Browse Combos"
    override val homeSectionShopByCategory = "Shop by Category"
    override val homeSectionAllItems = "All Items"
    override val homeSearchPlaceholder = "Search menu, items, categories…"

    // Categories
    override val categoriesTitle = "Categories"
    override val categoriesSubtitle = "Tap a category to filter the menu"
    override val categoriesAllItems = "All Items"
    override val itemCountOne = "1 item"
    override val itemCountMany = { n: Int -> "$n items" }
    override val categoriesEmptyTitle = "No categories"
    override val categoriesEmptyDescription = "The menu is empty."

    // Category products
    override val categoryNoItemsTitle = { c: String -> "No items in $c" }
    override val categoryNoItemsDescription = "Add products to this category from the catalog."

    // Product detail
    override val productDescription = "Description"
    override val productQuickFacts = "Quick Facts"
    override val productCategory = "Category"
    override val productItemCode = "Item code"
    override val productAvailability = "Availability"
    override val productInStock = "In stock"
    override val productOutOfStock = "Out of stock"
    override val productPrepTime = "Prep time"
    override val productPrepTimeValue = "5–8 min"
    override val productReviewsSuffix = "(120 reviews)"
    override val productAddToOrderTemplate = { total: String -> "Add $total" }
    override val productGenericDescriptionFallback =
        { category: String -> "Freshly prepared. Made to order. Our most-loved ${category.lowercase()} on the menu." }

    // Cart
    override val cartTitle = "Your Order"
    override val cartClear = "Clear"
    override val cartEmptyTitle = "Your cart is empty"
    override val cartEmptyDescription = "Add items from the menu to start an order."
    override val cartEmptyCta = "Browse Menu"
    override val cartProceedToCheckout = "Proceed to Checkout"
    override val cartSubtotal = "Subtotal"
    override val cartTax = "Tax (14%)"
    override val cartDiscount = "Discount"
    override val cartTotal = "Total"

    // Checkout
    override val checkoutTitle = "Checkout"
    override val checkoutOrderSummary = "Order Summary"
    override val checkoutCounter = "Counter"
    override val checkoutCounterValue = "Sahm Food · Counter 1"
    override val checkoutCounterLocation = "Cairo · Egypt"
    override val checkoutPaymentMethod = "Payment Method"
    override val checkoutCash = "Cash"
    override val checkoutCashDescription = "Pay with physical cash at counter"
    override val checkoutCard = "Card"
    override val checkoutCardDescription = "Tap, chip, or swipe"
    override val checkoutOrderNotes = "Order Notes"
    override val checkoutOrderNotesHint = "Add notes for the kitchen (optional)"
    override val checkoutTotalAmount = "Total Amount"
    override val checkoutConfirmCash = "Confirm Cash Payment"
    override val checkoutConfirmCard = "Confirm Card Payment"
    override val checkoutTapCardTitle = "Tap Card on Terminal"
    override val checkoutTapCardHint = "Hold card or device near the payment terminal."
    override val checkoutTerminalReady = "Terminal ready"

    // Receipt
    override val receiptPaymentSuccessful = "Payment Successful"
    override val receiptOrderNumberPrefix = "Order #"
    override val receiptTrackOrder = "Track Order"
    override val receiptReprint = "Reprint"
    override val receiptNewOrder = "New Order"

    // Tracking
    override val trackingTitle = "Order Tracking"
    override val trackingLiveStatus = "Live kitchen status"
    override val trackingEstimatedTime = "Estimated time"
    override val trackingEstimatedTimeValue = "5–8 minutes"
    override val trackingLastUpdate = "Last update"
    override val trackingStageReceived = "Received"
    override val trackingStagePreparing = "Preparing"
    override val trackingStageReady = "Ready"
    override val trackingStatusReceived = "Order received by kitchen"
    override val trackingStatusPreparing = "Kitchen is preparing your order"
    override val trackingStatusReady = "Order is ready for pickup"

    // History
    override val historyTitle = "Order History"
    override val historyOrdersToday = "Orders Today"
    override val historyRevenueToday = "Revenue Today"
    override val historyFilterAll = "All"
    override val historyFilterCash = "Cash"
    override val historyFilterCard = "Card"
    override val historyFilterSynced = "Synced"
    override val historyFilterPending = "Pending"
    override val historyEmpty = "No orders yet"
    override val historyOrderHashPrefix = "Order #"
    override val historyStatusSynced = "Synced"
    override val historyStatusPending = "Pending"
    override val historyStatusFailed = "Failed"
    override val historyStatusDraft = "Draft"

    // Favorites
    override val favoritesTitle = "My Favorites"
    override val favoritesEmptyTitle = "No favorites yet"
    override val favoritesEmptyDescription = "Tap the heart on any item to pin it here for quick one-tap access."
    override val favoritesBrowseMenu = "Browse Menu"

    // AI
    override val aiAssistantTitle = "AI Assistant"
    override val aiAssistantOnline = "Online"
    override val aiGreeting = "Hi! I'm your Sahm AI assistant. Ask me about today's sales, popular items, or pending orders."
    override val aiInputHint = "Ask me anything…"
    override val aiQuickBestSellers = "Best sellers today"
    override val aiQuickPending = "Pending orders"
    override val aiQuickRevenue = "Today's revenue"
    override val aiQuickSlowest = "Slowest item"
    override val aiQuickRecommend = "Recommend a dish"
    override val aiQuickSearchBurger = "Find me a burger"
    override val aiQuickBestSellersPrompt = "What are the best-selling items today?"
    override val aiQuickPendingPrompt = "How many orders are still being prepared?"
    override val aiQuickRevenuePrompt = "What is today's total revenue?"
    override val aiQuickSlowestPrompt = "Which item sold the least today?"
    override val aiQuickRecommendPrompt = "What should I order right now?"
    override val aiQuickSearchBurgerPrompt = "Find me a burger"

    // Profile
    override val profileGreeting = "Kareem · Counter 1"
    override val profileSubtitle = "Sahm Food · Cairo"
    override val profileFavorites = "Favorites"
    override val profileAiAssistant = "AI Assistant"
    override val profileSectionAccount = "Account"
    override val profileSectionApp = "App"
    override val profileSectionSession = "Session"
    override val profileRowSwitchRegister = "Switch register"
    override val profileRowPrinter = "Printer settings"
    override val profileRowPreferences = "Preferences"
    override val profileRowLanguage = "Language"
    override val profileRowTheme = "Theme"
    override val profileRowHelp = "Help & support"
    override val profileRowAbout = "About Sahm POS"
    override val profileRowEndShift = "End shift"
    override val profileAboutVersionSuffix = "v1.0"

    // Theme picker
    override val themePickerTitle = "Choose Theme"
    override val themeLight = "Light"
    override val themeDark = "Dark"
    override val themeSystem = "Follow System"

    // Language picker
    override val languagePickerTitle = "Choose Language"

    // About
    override val aboutAppName = "Sahm Food POS"
    override val aboutVersionLine = "Version 1.0.0 · Build 2026.05.001"
    override val aboutBuildLabel = "Build"
    override val aboutBuildValue = "debug"
    override val aboutKotlinLabel = "Kotlin"
    override val aboutComposeLabel = "Compose Multiplatform"
    override val aboutSqlDelightLabel = "Room"
    override val aboutKoinLabel = "Koin"
    override val aboutGithubLink = "View source on GitHub"
    override val aboutCopyright = "© 2026 Sahm Food. All rights reserved."

    // Switch register
    override val switchRegisterTitle = "Switch Register"
    override val switchRegisterOnline = "Online"
    override val switchRegisterOffline = "Offline"

    // Printer
    override val printerTitle = "Printer Settings"
    override val printerConnectedHeader = "Star TSP-100 (Mock)"
    override val printerConnected = "Connected"
    override val printerAvailable = "Available Printers"
    override val printerPrintOptions = "Print Options"
    override val printerAutoPrint = "Auto-print on payment"
    override val printerAutoPrintDesc = "Receipt prints automatically after each sale"
    override val printerIncludeLogo = "Include logo on receipt"
    override val printerIncludeLogoDesc = "Print Sahm Food logo header"
    override val printerCustomerCopy = "Print customer copy"
    override val printerCustomerCopyDesc = "Second receipt for the customer"
    override val printerTestReceipt = "Print Test Receipt"

    // Preferences
    override val preferencesTitle = "Preferences"
    override val preferencesInteraction = "Interaction"
    override val preferencesSoundFeedback = "Sound feedback"
    override val preferencesSoundFeedbackDesc = "Play tap and success sounds"
    override val preferencesHaptic = "Haptic feedback"
    override val preferencesHapticDesc = "Vibrate on key actions"
    override val preferencesSafety = "Safety"
    override val preferencesConfirmDelete = "Confirm before deleting cart items"
    override val preferencesConfirmDeleteDesc = "Show a confirmation dialog"
    override val preferencesStockWarnings = "Show stock warnings"
    override val preferencesStockWarningsDesc = "Alert when an item is low on inventory"
    override val preferencesSession = "Session"
    override val preferencesAutoLogout = "Auto end shift after 12 hours"
    override val preferencesAutoLogoutDesc = "Force log-out after a long shift"
    override val preferencesDefaults = "Defaults"
    override val preferencesDefaultPayment = "Default payment method"
    override val preferencesDefaultPaymentValue = "Cash"
    override val preferencesDefaultTax = "Default tax rate"
    override val preferencesDefaultTaxValue = "14%"
    override val preferencesReceiptWidth = "Receipt width"
    override val preferencesReceiptWidthValue = "32 chars"
    override val preferencesCurrency = "Currency"
    override val preferencesCurrencyValue = "EGP"

    // Help
    override val helpTitle = "Help & Support"
    override val helpGetInTouch = "Get in touch"
    override val helpCallSupport = "Call support"
    override val helpCallSupportValue = "+20 100 555 7777"
    override val helpEmailSupport = "Email support"
    override val helpEmailSupportValue = "support@sahmfood.com"
    override val helpLiveChat = "Live chat"
    override val helpLiveChatValue = "Available 9am–11pm"
    override val helpResources = "Resources"
    override val helpCashierHandbook = "Cashier handbook"
    override val helpCashierHandbookDesc = "Step-by-step guide to common workflows"
    override val helpTroubleshooting = "Troubleshooting"
    override val helpTroubleshootingDesc = "Printer, network, and sync issues"
    override val helpFaq = "Frequently asked"
    override val helpFaqRefundQ = "How do I refund an order?"
    override val helpFaqRefundA = "Open the Orders tab, find the order, and tap Refund. A manager PIN may be required."
    override val helpFaqPrinterQ = "Why is the printer offline?"
    override val helpFaqPrinterA = "Check the printer power and the Wi-Fi connection. Re-pair from Profile → Printer Settings."
    override val helpFaqOfflineQ = "Can I work offline?"
    override val helpFaqOfflineA = "Yes — all orders sync automatically when the device comes back online."

    // Common
    override val commonBack = "Back"
    override val commonNotifications = "Notifications"
    override val expressBadge = "Express"
}
