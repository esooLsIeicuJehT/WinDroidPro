package com.windroidpro

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class WinDroidApplication : Application() {

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "windroid_service"
        const val NOTIFICATION_CHANNEL_NAME = "WinDroid Service"
        
        init {
            System.loadLibrary("windroidpro")
        }
    }

    override fun onCreate() {
        super.onCreate()
        
        // Initialize Timber for logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        
        // Create notification channel for foreground service
        createNotificationChannel()
        
        Timber.d("WinDroid Pro Application initialized")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "WinDroid Pro emulation service"
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}