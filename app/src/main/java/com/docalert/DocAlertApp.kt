package com.docalert

import android.app.Application
import com.docalert.util.AdManager
import com.docalert.util.BillingManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@HiltAndroidApp
class DocAlertApp : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    val billingManager by lazy { BillingManager(this) }

    override fun onCreate() {
        super.onCreate()
        AdManager.initialize(this)

        val prefs = getSharedPreferences("docalert_prefs", MODE_PRIVATE)
        val isPremium = prefs.getBoolean("is_premium", false)
        val adFreeUntil = prefs.getLong("ad_free_until", 0)

        if (isPremium || (adFreeUntil > System.currentTimeMillis())) {
            AdManager.setPremium(true)
        }

        applicationScope.launch {
            billingManager.isPremium.collect { premium ->
                if (premium) {
                    AdManager.setPremium(true)
                    prefs.edit().putBoolean("is_premium", true).apply()
                }
            }
        }
    }
}
