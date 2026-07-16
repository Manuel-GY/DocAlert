package com.docalert.util

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BillingManager(context: Context) : PurchasesUpdatedListener {
    private val billingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases()
        .build()

    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    private val PRODUCT_ID = "docalert_premium"

    init {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    checkExistingPurchases()
                }
            }
            override fun onBillingServiceDisconnected() {}
        })
    }

    private fun checkExistingPurchases() {
        billingClient.queryPurchasesAsync(
            QueryProductDetailsParams.newBuilder()
                .setProductList(listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(PRODUCT_ID)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                ))
                .build()
        ) { result, purchases ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                purchases?.forEach { purchase ->
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        _isPremium.value = true
                        acknowledgePurchase(purchase)
                    }
                }
            }
        }
    }

    fun launchPremiumPurchase(activity: Activity) {
        billingClient.queryProductDetailsAsync(
            QueryProductDetailsParams.newBuilder()
                .setProductList(listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(PRODUCT_ID)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                ))
                .build()
        ) { result, productDetailsList ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK && productDetailsList.isNotEmpty()) {
                val flowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(
                        listOf(
                            BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetailsList[0])
                                .build()
                        )
                    )
                    .build()
                billingClient.launchBillingFlow(activity, flowParams)
            }
        }
    }

    override fun onPurchasesUpdated(result: BillingResult, purchases: List<Purchase>?) {
        if (result.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            purchases.forEach { purchase ->
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    _isPremium.value = true
                    acknowledgePurchase(purchase)
                }
            }
        }
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        if (!purchase.isAcknowledged) {
            val params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
            billingClient.acknowledgePurchase(params) { }
        }
    }
}
