package com.sahmfood.pos.ui.strings

object ArabicStrings : SahmStrings {
    // Bottom nav
    override val navHome = "الرئيسية"
    override val navCart = "السلة"
    override val navCategories = "الأقسام"
    override val navOrders = "الطلبات"
    override val navProfile = "حسابي"

    // Home
    override val homeStoreName = "سهم فود"
    override val homeCashierLabel = "الكاشير"
    override val homeCashierValue = "سهم فود · كاونتر 1"
    override val homeBannerTitle = "عروض اليوم"
    override val homeBannerSubtitle = "خصم 20٪ على وجبات الكومبو · إكسبريس فقط"
    override val homeBannerCta = "تصفح العروض"
    override val homeSectionShopByCategory = "تسوق بالأقسام"
    override val homeSectionAllItems = "جميع الأصناف"
    override val homeSearchPlaceholder = "ابحث عن صنف أو قسم…"

    // Categories
    override val categoriesTitle = "الأقسام"
    override val categoriesSubtitle = "اختر قسماً لتصفية القائمة"
    override val categoriesAllItems = "جميع الأصناف"
    override val itemCountOne = "صنف واحد"
    override val itemCountMany = { n: Int ->
        when (n) {
            0 -> "لا توجد أصناف"
            1 -> "صنف واحد"
            2 -> "صنفان"
            in 3..10 -> "$n أصناف"
            else -> "$n صنفاً"
        }
    }
    override val categoriesEmptyTitle = "لا توجد أقسام"
    override val categoriesEmptyDescription = "القائمة فارغة."

    // Category products
    override val categoryNoItemsTitle = { c: String -> "لا توجد أصناف في $c" }
    override val categoryNoItemsDescription = "أضف منتجات لهذا القسم من القائمة."

    // Product detail
    override val productDescription = "الوصف"
    override val productQuickFacts = "معلومات سريعة"
    override val productCategory = "القسم"
    override val productItemCode = "كود الصنف"
    override val productAvailability = "التوفر"
    override val productInStock = "متوفر"
    override val productOutOfStock = "غير متوفر"
    override val productPrepTime = "وقت التحضير"
    override val productPrepTimeValue = "5–8 دقائق"
    override val productReviewsSuffix = "(120 تقييم)"
    override val productAddToOrderTemplate = { total: String -> "أضف $total" }
    override val productGenericDescriptionFallback =
        { _: String -> "يُحضّر طازجاً عند الطلب. من أكثر أصنافنا طلباً." }

    // Cart
    override val cartTitle = "طلبك"
    override val cartClear = "مسح"
    override val cartEmptyTitle = "سلتك فارغة"
    override val cartEmptyDescription = "أضف أصنافاً من القائمة لبدء طلب."
    override val cartEmptyCta = "تصفح القائمة"
    override val cartProceedToCheckout = "المتابعة للدفع"
    override val cartSubtotal = "المجموع الفرعي"
    override val cartTax = "الضريبة (14٪)"
    override val cartDiscount = "الخصم"
    override val cartTotal = "الإجمالي"

    // Checkout
    override val checkoutTitle = "الدفع"
    override val checkoutOrderSummary = "ملخص الطلب"
    override val checkoutCounter = "الكاونتر"
    override val checkoutCounterValue = "سهم فود · كاونتر 1"
    override val checkoutCounterLocation = "القاهرة · مصر"
    override val checkoutPaymentMethod = "طريقة الدفع"
    override val checkoutCash = "نقداً"
    override val checkoutCashDescription = "ادفع نقداً عند الكاونتر"
    override val checkoutCard = "بطاقة"
    override val checkoutCardDescription = "تماس، شريحة، أو تمرير"
    override val checkoutOrderNotes = "ملاحظات الطلب"
    override val checkoutOrderNotesHint = "أضف ملاحظات للمطبخ (اختياري)"
    override val checkoutTotalAmount = "المبلغ الإجمالي"
    override val checkoutConfirmCash = "تأكيد الدفع نقداً"
    override val checkoutConfirmCard = "تأكيد الدفع بالبطاقة"
    override val checkoutTapCardTitle = "مرر البطاقة على الجهاز"
    override val checkoutTapCardHint = "قرّب البطاقة أو الهاتف من جهاز الدفع."
    override val checkoutTerminalReady = "الجهاز جاهز"

    // Receipt
    override val receiptPaymentSuccessful = "تم الدفع بنجاح"
    override val receiptOrderNumberPrefix = "طلب رقم "
    override val receiptTrackOrder = "تتبع الطلب"
    override val receiptReprint = "إعادة طباعة"
    override val receiptNewOrder = "طلب جديد"

    // Tracking
    override val trackingTitle = "تتبع الطلب"
    override val trackingLiveStatus = "حالة المطبخ المباشرة"
    override val trackingEstimatedTime = "الوقت المتوقع"
    override val trackingEstimatedTimeValue = "5–8 دقائق"
    override val trackingLastUpdate = "آخر تحديث"
    override val trackingStageReceived = "مُستلم"
    override val trackingStagePreparing = "قيد التحضير"
    override val trackingStageReady = "جاهز"
    override val trackingStatusReceived = "تم استلام الطلب في المطبخ"
    override val trackingStatusPreparing = "المطبخ يحضّر طلبك"
    override val trackingStatusReady = "الطلب جاهز للاستلام"

    // History
    override val historyTitle = "سجل الطلبات"
    override val historyOrdersToday = "طلبات اليوم"
    override val historyRevenueToday = "إيرادات اليوم"
    override val historyFilterAll = "الكل"
    override val historyFilterCash = "نقداً"
    override val historyFilterCard = "بطاقة"
    override val historyFilterSynced = "متزامن"
    override val historyFilterPending = "قيد الانتظار"
    override val historyEmpty = "لا توجد طلبات بعد"
    override val historyOrderHashPrefix = "طلب رقم "
    override val historyStatusSynced = "متزامن"
    override val historyStatusPending = "قيد الانتظار"
    override val historyStatusFailed = "فشل"
    override val historyStatusDraft = "مسودة"

    // Favorites
    override val favoritesTitle = "المفضلة"
    override val favoritesEmptyTitle = "لا توجد مفضلات بعد"
    override val favoritesEmptyDescription = "اضغط القلب على أي صنف لتثبيته هنا لإضافة سريعة."
    override val favoritesBrowseMenu = "تصفح القائمة"

    // AI
    override val aiAssistantTitle = "المساعد الذكي"
    override val aiAssistantOnline = "متصل"
    override val aiGreeting = "أهلاً! أنا مساعدك الذكي. اسألني عن مبيعات اليوم، الأصناف الشائعة، أو الطلبات المعلّقة."
    override val aiInputHint = "اسألني أي شيء…"
    override val aiQuickBestSellers = "الأكثر مبيعاً اليوم"
    override val aiQuickPending = "الطلبات المعلّقة"
    override val aiQuickRevenue = "إيرادات اليوم"
    override val aiQuickSlowest = "الأقل مبيعاً"
    override val aiQuickBestSellersPrompt = "ما هي الأصناف الأكثر مبيعاً اليوم؟"
    override val aiQuickPendingPrompt = "كم عدد الطلبات المعلّقة؟"
    override val aiQuickRevenuePrompt = "كم إجمالي إيرادات اليوم؟"
    override val aiQuickSlowestPrompt = "ما الصنف الأقل مبيعاً اليوم؟"

    // Profile
    override val profileGreeting = "كريم · كاونتر 1"
    override val profileSubtitle = "سهم فود · القاهرة"
    override val profileFavorites = "المفضلة"
    override val profileAiAssistant = "المساعد الذكي"
    override val profileSectionAccount = "الحساب"
    override val profileSectionApp = "التطبيق"
    override val profileSectionSession = "الجلسة"
    override val profileRowSwitchRegister = "تبديل الكاونتر"
    override val profileRowPrinter = "إعدادات الطابعة"
    override val profileRowPreferences = "التفضيلات"
    override val profileRowLanguage = "اللغة"
    override val profileRowTheme = "المظهر"
    override val profileRowHelp = "المساعدة والدعم"
    override val profileRowAbout = "عن سهم"
    override val profileRowEndShift = "إنهاء الوردية"
    override val profileAboutVersionSuffix = "النسخة 1.0"

    // Theme picker
    override val themePickerTitle = "اختر المظهر"
    override val themeLight = "فاتح"
    override val themeDark = "داكن"
    override val themeSystem = "حسب النظام"

    // Language picker
    override val languagePickerTitle = "اختر اللغة"

    // About
    override val aboutAppName = "سهم فود"
    override val aboutVersionLine = "النسخة 1.0.0 · بناء 2026.05.001"
    override val aboutBuildLabel = "البناء"
    override val aboutBuildValue = "تجريبي"
    override val aboutKotlinLabel = "Kotlin"
    override val aboutComposeLabel = "Compose Multiplatform"
    override val aboutSqlDelightLabel = "Room"
    override val aboutKoinLabel = "Koin"
    override val aboutGithubLink = "عرض المصدر على GitHub"
    override val aboutCopyright = "© 2026 سهم فود. جميع الحقوق محفوظة."

    // Switch register
    override val switchRegisterTitle = "تبديل الكاونتر"
    override val switchRegisterOnline = "متصل"
    override val switchRegisterOffline = "غير متصل"

    // Printer
    override val printerTitle = "إعدادات الطابعة"
    override val printerConnectedHeader = "Star TSP-100 (نموذج)"
    override val printerConnected = "متصل"
    override val printerAvailable = "الطابعات المتاحة"
    override val printerPrintOptions = "خيارات الطباعة"
    override val printerAutoPrint = "طباعة تلقائية عند الدفع"
    override val printerAutoPrintDesc = "تتم طباعة الإيصال تلقائياً بعد كل عملية بيع"
    override val printerIncludeLogo = "تضمين الشعار على الإيصال"
    override val printerIncludeLogoDesc = "طباعة شعار سهم فود في رأس الإيصال"
    override val printerCustomerCopy = "طباعة نسخة العميل"
    override val printerCustomerCopyDesc = "إيصال ثانٍ للعميل"
    override val printerTestReceipt = "طباعة إيصال تجريبي"

    // Preferences
    override val preferencesTitle = "التفضيلات"
    override val preferencesInteraction = "التفاعل"
    override val preferencesSoundFeedback = "صوت التنبيه"
    override val preferencesSoundFeedbackDesc = "تشغيل أصوات النقر والنجاح"
    override val preferencesHaptic = "اهتزاز اللمس"
    override val preferencesHapticDesc = "اهتزاز عند الإجراءات الأساسية"
    override val preferencesSafety = "الأمان"
    override val preferencesConfirmDelete = "تأكيد قبل حذف أصناف السلة"
    override val preferencesConfirmDeleteDesc = "إظهار حوار تأكيد"
    override val preferencesStockWarnings = "تنبيهات المخزون"
    override val preferencesStockWarningsDesc = "تنبيه عند انخفاض كمية صنف ما"
    override val preferencesSession = "الجلسة"
    override val preferencesAutoLogout = "إنهاء الوردية تلقائياً بعد 12 ساعة"
    override val preferencesAutoLogoutDesc = "تسجيل خروج إجباري بعد وردية طويلة"
    override val preferencesDefaults = "الإعدادات الافتراضية"
    override val preferencesDefaultPayment = "طريقة الدفع الافتراضية"
    override val preferencesDefaultPaymentValue = "نقداً"
    override val preferencesDefaultTax = "نسبة الضريبة الافتراضية"
    override val preferencesDefaultTaxValue = "14٪"
    override val preferencesReceiptWidth = "عرض الإيصال"
    override val preferencesReceiptWidthValue = "32 حرفاً"
    override val preferencesCurrency = "العملة"
    override val preferencesCurrencyValue = "ج.م"

    // Help
    override val helpTitle = "المساعدة والدعم"
    override val helpGetInTouch = "تواصل معنا"
    override val helpCallSupport = "اتصل بالدعم"
    override val helpCallSupportValue = "+20 100 555 7777"
    override val helpEmailSupport = "البريد الإلكتروني"
    override val helpEmailSupportValue = "support@sahmfood.com"
    override val helpLiveChat = "محادثة مباشرة"
    override val helpLiveChatValue = "متاح من 9 صباحاً حتى 11 مساءً"
    override val helpResources = "الموارد"
    override val helpCashierHandbook = "دليل الكاشير"
    override val helpCashierHandbookDesc = "دليل خطوة بخطوة لسير العمل الشائع"
    override val helpTroubleshooting = "حل المشكلات"
    override val helpTroubleshootingDesc = "مشكلات الطابعة، الشبكة، والمزامنة"
    override val helpFaq = "الأسئلة الشائعة"
    override val helpFaqRefundQ = "كيف أرد قيمة طلب؟"
    override val helpFaqRefundA = "افتح تبويب الطلبات، اختر الطلب، واضغط استرداد. قد يلزم كود مدير."
    override val helpFaqPrinterQ = "لماذا الطابعة غير متصلة؟"
    override val helpFaqPrinterA = "تحقق من تشغيل الطابعة والاتصال بالواي فاي. أعد الإقران من حسابي ← إعدادات الطابعة."
    override val helpFaqOfflineQ = "هل يمكنني العمل بدون إنترنت؟"
    override val helpFaqOfflineA = "نعم — تتم مزامنة جميع الطلبات تلقائياً عند عودة الاتصال."

    // Common
    override val commonBack = "رجوع"
    override val commonNotifications = "الإشعارات"
    override val expressBadge = "إكسبريس"
}
