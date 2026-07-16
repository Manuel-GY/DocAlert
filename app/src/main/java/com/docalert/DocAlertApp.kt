package com.docalert

import android.app.Application
import com.docalert.util.AdManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DocAlertApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AdManager.initialize(this)

        // Cargar estado premium
        val prefs = getSharedPreferences("docalert_prefs", MODE_PRIVATE)
        val isPremium = prefs.getBoolean("is_premium", false)
        val adFreeUntil = prefs.getLong("ad_free_until", 0)

        if (isPremium || (adFreeUntil > System.currentTimeMillis())) {
            AdManager.setPremium(true)
        }
    }
}
