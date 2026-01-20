package com.windroidpro.wine

import android.app.Service
import android.content.Intent
import android.os.IBinder
import timber.log.Timber

class WineService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Timber.d("WineService created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("WineService started")
        return START_NOT_STICKY
    }
}
