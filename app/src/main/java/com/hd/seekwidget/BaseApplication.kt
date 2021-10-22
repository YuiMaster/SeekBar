package com.hd.seekwidget

import android.app.Application

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContext.init(this)
    }
}