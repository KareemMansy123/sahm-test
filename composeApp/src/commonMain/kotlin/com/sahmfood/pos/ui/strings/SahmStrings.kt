package com.sahmfood.pos.ui.strings

/**
 * Every user-visible string in the app. One interface per language is
 * implemented by [EnglishStrings] / [ArabicStrings]. UI reads through
 * [LocalSahmStrings] which the root composable populates from the
 * AppSettingsStore.
 *
 * Why not Compose-MPP XML resources? Two reasons:
 *  1. Sahm POS needs runtime language switching without restarting
 *     the activity. Compose-MPP's stringResource reads the platform
 *     locale; overriding it cleanly per-composition is the same effort
 *     as this approach but uglier.
 *  2. Type-safety. Every string is a member of the interface; the
 *     compiler ensures both languages cover every key. No "missing
 *     translation" runtime failure.
 */
interface SahmStrings {
    // Bottom navigation
    val navHome: String
    val navCart: String
    val navCategories: String
    val navOrders: String
    val navProfile: String

    // Home / catalog
    val homeStoreName: String
    val homeCashierLabel: String
    val homeCashierValue: String
    val homeBannerTitle: String
    val homeBannerSubtitle: String
    val homeBannerCta: String
    val homeSectionShopByCategory: String
    val homeSectionAllItems: String
    val homeSearchPlaceholder: String

    // Categories tab
    val categoriesTitle: String
    val categoriesSubtitle: String
    val categoriesAllItems: String
    val itemCountOne: String
    val itemCountMany: (Int) -> String
    val categoriesEmptyTitle: String
    val categoriesEmptyDescription: String

    // Category products screen
    val categoryNoItemsTitle: (String) -> String
    val categoryNoItemsDescription: String

    // Product detail
    val productDescription: String
    val productQuickFacts: String
    val productCategory: String
    val productItemCode: String
    val productAvailability: String
    val productInStock: String
    val productOutOfStock: String
    val productPrepTime: String
    val productPrepTimeValue: String
    val productReviewsSuffix: String
    val productAddToOrderTemplate: (String) -> String
    val productGenericDescriptionFallback: (String) -> String

    // Cart
    val cartTitle: String
    val cartClear: String
    val cartEmptyTitle: String
    val cartEmptyDescription: String
    val cartEmptyCta: String
    val cartProceedToCheckout: String
    val cartSubtotal: String
    val cartTax: String
    val cartDiscount: String
    val cartTotal: String

    // Checkout
    val checkoutTitle: String
    val checkoutOrderSummary: String
    val checkoutCounter: String
    val checkoutCounterValue: String
    val checkoutCounterLocation: String
    val checkoutPaymentMethod: String
    val checkoutCash: String
    val checkoutCashDescription: String
    val checkoutCard: String
    val checkoutCardDescription: String
    val checkoutOrderNotes: String
    val checkoutOrderNotesHint: String
    val checkoutTotalAmount: String
    val checkoutConfirmCash: String
    val checkoutConfirmCard: String
    val checkoutTapCardTitle: String
    val checkoutTapCardHint: String
    val checkoutTerminalReady: String

    // Receipt / success
    val receiptPaymentSuccessful: String
    val receiptOrderNumberPrefix: String
    val receiptTrackOrder: String
    val receiptReprint: String
    val receiptNewOrder: String

    // Order tracking
    val trackingTitle: String
    val trackingLiveStatus: String
    val trackingEstimatedTime: String
    val trackingEstimatedTimeValue: String
    val trackingLastUpdate: String
    val trackingStageReceived: String
    val trackingStagePreparing: String
    val trackingStageReady: String
    val trackingStatusReceived: String
    val trackingStatusPreparing: String
    val trackingStatusReady: String

    // History
    val historyTitle: String
    val historyOrdersToday: String
    val historyRevenueToday: String
    val historyFilterAll: String
    val historyFilterCash: String
    val historyFilterCard: String
    val historyFilterSynced: String
    val historyFilterPending: String
    val historyEmpty: String
    val historyOrderHashPrefix: String
    val historyStatusSynced: String
    val historyStatusPending: String
    val historyStatusFailed: String
    val historyStatusDraft: String

    // Favorites
    val favoritesTitle: String
    val favoritesEmptyTitle: String
    val favoritesEmptyDescription: String
    val favoritesBrowseMenu: String

    // AI assistant
    val aiAssistantTitle: String
    val aiAssistantOnline: String
    val aiGreeting: String
    val aiInputHint: String
    val aiQuickBestSellers: String
    val aiQuickPending: String
    val aiQuickRevenue: String
    val aiQuickSlowest: String
    val aiQuickRecommend: String
    val aiQuickSearchBurger: String
    val aiQuickBestSellersPrompt: String
    val aiQuickPendingPrompt: String
    val aiQuickRevenuePrompt: String
    val aiQuickSlowestPrompt: String
    val aiQuickRecommendPrompt: String
    val aiQuickSearchBurgerPrompt: String

    // Profile
    val profileGreeting: String
    val profileSubtitle: String
    val profileFavorites: String
    val profileAiAssistant: String
    val profileSectionAccount: String
    val profileSectionApp: String
    val profileSectionSession: String
    val profileRowSwitchRegister: String
    val profileRowPrinter: String
    val profileRowPreferences: String
    val profileRowLanguage: String
    val profileRowTheme: String
    val profileRowHelp: String
    val profileRowAbout: String
    val profileRowEndShift: String
    val profileAboutVersionSuffix: String

    // Theme picker
    val themePickerTitle: String
    val themeLight: String
    val themeDark: String
    val themeSystem: String

    // Language picker
    val languagePickerTitle: String

    // About
    val aboutAppName: String
    val aboutVersionLine: String
    val aboutBuildLabel: String
    val aboutBuildValue: String
    val aboutKotlinLabel: String
    val aboutComposeLabel: String
    val aboutSqlDelightLabel: String
    val aboutKoinLabel: String
    val aboutGithubLink: String
    val aboutCopyright: String

    // Switch register
    val switchRegisterTitle: String
    val switchRegisterOnline: String
    val switchRegisterOffline: String

    // Printer
    val printerTitle: String
    val printerConnectedHeader: String
    val printerConnected: String
    val printerAvailable: String
    val printerPrintOptions: String
    val printerAutoPrint: String
    val printerAutoPrintDesc: String
    val printerIncludeLogo: String
    val printerIncludeLogoDesc: String
    val printerCustomerCopy: String
    val printerCustomerCopyDesc: String
    val printerTestReceipt: String

    // Preferences
    val preferencesTitle: String
    val preferencesInteraction: String
    val preferencesSoundFeedback: String
    val preferencesSoundFeedbackDesc: String
    val preferencesHaptic: String
    val preferencesHapticDesc: String
    val preferencesSafety: String
    val preferencesConfirmDelete: String
    val preferencesConfirmDeleteDesc: String
    val preferencesStockWarnings: String
    val preferencesStockWarningsDesc: String
    val preferencesSession: String
    val preferencesAutoLogout: String
    val preferencesAutoLogoutDesc: String
    val preferencesDefaults: String
    val preferencesDefaultPayment: String
    val preferencesDefaultPaymentValue: String
    val preferencesDefaultTax: String
    val preferencesDefaultTaxValue: String
    val preferencesReceiptWidth: String
    val preferencesReceiptWidthValue: String
    val preferencesCurrency: String
    val preferencesCurrencyValue: String

    // Help & support
    val helpTitle: String
    val helpGetInTouch: String
    val helpCallSupport: String
    val helpCallSupportValue: String
    val helpEmailSupport: String
    val helpEmailSupportValue: String
    val helpLiveChat: String
    val helpLiveChatValue: String
    val helpResources: String
    val helpCashierHandbook: String
    val helpCashierHandbookDesc: String
    val helpTroubleshooting: String
    val helpTroubleshootingDesc: String
    val helpFaq: String
    val helpFaqRefundQ: String
    val helpFaqRefundA: String
    val helpFaqPrinterQ: String
    val helpFaqPrinterA: String
    val helpFaqOfflineQ: String
    val helpFaqOfflineA: String

    // Common
    val commonBack: String
    val commonNotifications: String
    val expressBadge: String
}
