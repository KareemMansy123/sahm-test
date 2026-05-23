package com.sahmfood.pos

import android.app.Application
import com.sahmfood.pos.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level

class SahmPosApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger(Level.ERROR)
            androidContext(this@SahmPosApplication)
        }
    }
}
