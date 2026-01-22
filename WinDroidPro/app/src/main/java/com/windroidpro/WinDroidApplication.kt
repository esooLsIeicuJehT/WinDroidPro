package com.windroidpro

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.windroidpro.usb.NativeUsbManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.system.measureTimeMillis

@HiltAndroidApp
class WinDroidApplication : Application() {

    // Scope for application-wide background tasks that should outlive any specific activity
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "windroid_service"
        const val NOTIFICATION_CHANNEL_NAME = "WinDroid Service"
    }

    override fun onCreate() {
        super.onCreate()
        
        // Load the native library asynchronously
        NativeUsbManager.loadLibraryAsync()

        // Initialize Timber for logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        
        // Create notification channel for foreground service off the main thread
        applicationScope.launch(Dispatchers.IO) {
            val time = measureTimeMillis {
                createNotificationChannel()
            }
            Timber.d("Notification channel created in ${time}ms on background thread")
        }
        
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