package com.windroidpro.usb

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbManager
import timber.log.Timber

class UsbDeviceReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        Timber.d("USB Receiver received action: $action")

        when (action) {
            UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                Timber.i("USB Device attached")
            }
            UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                Timber.i("USB Device detached")
            }
        }
    }
}
