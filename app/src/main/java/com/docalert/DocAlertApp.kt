package com.docalert

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DocAlertApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
