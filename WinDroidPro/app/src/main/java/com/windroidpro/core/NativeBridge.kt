package com.windroidpro.core

import timber.log.Timber

/**
 * Native bridge for interfacing with C++ code
 */
object NativeBridge {
    
    init {
        try {
            System.loadLibrary("windroidpro")
            Timber.d("Native library loaded successfully")
        } catch (e: UnsatisfiedLinkError) {
            Timber.e(e, "Failed to load native library")
        }
    }
    
    /**
     * Get native bridge version
     */
    external fun getVersion(): String
    
    /**
     * Initialize Wine environment
     */
    external fun initializeWine(winePrefix: String, wineArch: String): Boolean
    
    /**
     * Execute Windows application through Wine
     */
    external fun executeWineApp(exePath: String, args: String, workingDir: String): Int
    
    /**
     * Initialize Box64 for x86_64 translation
     */
    external fun initializeBox64(libPath: String): Boolean
    
    /**
     * Initialize USB subsystem
     */
    external fun initializeUSB(): Boolean
    
    /**
     * Attach USB device
     */
    external fun attachUSBDevice(vendorId: Int, productId: Int, fd: Int): Boolean
    
    /**
     * Detach USB device
     */
    external fun detachUSBDevice(deviceId: Int): Boolean
    
    /**
     * Cleanup native resources
     */
    external fun cleanup()
}