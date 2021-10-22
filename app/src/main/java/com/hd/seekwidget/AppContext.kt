package com.hd.seekwidget

import android.app.Application
import android.content.Context

/**
 * @Description: 应用context，默认懒加载,线程安全
 * @Author: liaoyuhuan
 * @CreateDate: 2020/12/7 0007
 * [appInitDone]应用已初始化
 */
object AppContext {
    private lateinit var context: Application

    // 应用启动间隔时间
    private var appInitDistanceTime = 0F

    fun init(context: Application) {
        this.context = context
    }

    fun getContext(): Context {
        return context
    }

    fun getApplication(): Application {
        return context
    }

    fun setAppInitDistanceTime(distanceTime: Float) {
        appInitDistanceTime = distanceTime
    }

    fun getAppInitDistanceTime(): Float {
        return appInitDistanceTime
    }

    // 应用已初始化
    var appInitDone = false
}