package com.bilgideham.app

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import com.example.bilgideham.DebugLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Google Play Billing yöneticisi
 * Uygulama içi satın alma ve abonelik işlemlerini yönetir
 */
class BillingManager(private val context: Context) : PurchasesUpdatedListener {

    companion object {
        private const val TAG = "BillingManager"
        
        // Ürün ID'leri - Google Play Console'da tanımlanmalı
        const val PRODUCT_PREMIUM_MONTHLY = "premium_monthly"
        const val PRODUCT_PREMIUM_YEARLY = "premium_yearly"
        const val PRODUCT_QUESTION_PACK = "question_pack_100"
        
        @Volatile
        private var INSTANCE: BillingManager? = null
        
        fun getInstance(context: Context): BillingManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: BillingManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    private var billingClient: BillingClient? = null
    
    // Premium durumu
    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()
    
    // Bağlantı durumu
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()
    
    // Ürün detayları
    private val _subscriptionProducts = MutableStateFlow<List<ProductDetails>>(emptyList())
    val subscriptionProducts: StateFlow<List<ProductDetails>> = _subscriptionProducts.asStateFlow()
    
    private val _oneTimeProducts = MutableStateFlow<List<ProductDetails>>(emptyList())
    val oneTimeProducts: StateFlow<List<ProductDetails>> = _oneTimeProducts.asStateFlow()

    init {
        setupBillingClient()
    }

    private fun setupBillingClient() {
        billingClient = BillingClient.newBuilder(context)
            .setListener(this)
            .enablePendingPurchases()
            .build()
        
        startConnection()
    }

    private fun startConnection() {
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    DebugLog.d(TAG, "✅ Billing bağlantısı başarılı")
                    _isConnected.value = true
                    queryProducts()
                    queryPurchases()
                } else {
                    Log.e(TAG, "❌ Billing bağlantı hatası: ${billingResult.debugMessage}")
                    _isConnected.value = false
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.w(TAG, "⚠️ Billing bağlantısı kesildi, yeniden bağlanılıyor...")
                _isConnected.value = false
                // Yeniden bağlan
                startConnection()
            }
        })
    }

    /**
     * Ürünleri sorgula
     */
    private fun queryProducts() {
        // Abonelik ürünleri
        val subscriptionParams = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(PRODUCT_PREMIUM_MONTHLY)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build(),
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(PRODUCT_PREMIUM_YEARLY)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
                )
            )
            .build()

        billingClient?.queryProductDetailsAsync(subscriptionParams) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                _subscriptionProducts.value = productDetailsList
                DebugLog.d(TAG, "📦 ${productDetailsList.size} abonelik ürünü bulundu")
            }
        }

        // Tek seferlik ürünler
        val oneTimeParams = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(PRODUCT_QUESTION_PACK)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                )
            )
            .build()

        billingClient?.queryProductDetailsAsync(oneTimeParams) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                _oneTimeProducts.value = productDetailsList
                DebugLog.d(TAG, "📦 ${productDetailsList.size} tek seferlik ürün bulundu")
            }
        }
    }

    /**
     * Mevcut satın almaları kontrol et
     */
    private fun queryPurchases() {
        // Abonelikleri kontrol et
        billingClient?.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        ) { billingResult, purchasesList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val hasActiveSub = purchasesList.any { 
                    it.purchaseState == Purchase.PurchaseState.PURCHASED 
                }
                _isPremium.value = hasActiveSub
                DebugLog.d(TAG, "👑 Premium durumu: $hasActiveSub")
                
                // Onaylanmamış satın almaları onayla
                purchasesList.forEach { purchase ->
                    if (!purchase.isAcknowledged) {
                        acknowledgePurchase(purchase)
                    }
                }
            }
        }
    }

    /**
     * Satın alma işlemini başlat
     */
    fun launchPurchaseFlow(activity: Activity, productDetails: ProductDetails, isSubscription: Boolean) {
        val offerToken = if (isSubscription) {
            productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken ?: return
        } else null

        val productDetailsParamsBuilder = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(productDetails)
        
        if (offerToken != null) {
            productDetailsParamsBuilder.setOfferToken(offerToken)
        }

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productDetailsParamsBuilder.build()))
            .build()

        billingClient?.launchBillingFlow(activity, billingFlowParams)
    }

    /**
     * Satın alma güncellendiğinde çağrılır
     */
    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                purchases?.forEach { purchase ->
                    handlePurchase(purchase)
                }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                DebugLog.d(TAG, "Kullanıcı satın almayı iptal etti")
            }
            else -> {
                Log.e(TAG, "Satın alma hatası: ${billingResult.debugMessage}")
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            // Premium'u aktifle
            _isPremium.value = true
            DebugLog.d(TAG, "🎉 Satın alma başarılı: ${purchase.products}")
            
            // Satın almayı onayla
            if (!purchase.isAcknowledged) {
                acknowledgePurchase(purchase)
            }
        }
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        billingClient?.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                DebugLog.d(TAG, "✅ Satın alma onaylandı")
            }
        }
    }

    /**
     * Bağlantıyı kapat
     */
    fun endConnection() {
        billingClient?.endConnection()
        _isConnected.value = false
    }

    /**
     * Premium içerik erişimi kontrolü
     */
    fun checkPremiumAccess(): Boolean {
        return _isPremium.value
    }
}
