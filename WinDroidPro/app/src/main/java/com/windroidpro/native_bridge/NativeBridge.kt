package com.windroidpro.native_bridge

import timber.log.Timber

/**
 * Native bridge for Wine, Box64, and USB integration
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
    
    // Native method declarations
    external fun getVersion(): String
    external fun initializeWine(winePrefix: String, wineArch: String): Boolean
    external fun executeWineApp(exePath: String, args: String, workingDir: String): Int
    external fun initializeBox64(libPath: String): Boolean
    external fun initializeUSB(): Boolean
    external fun attachUSBDevice(vendorId: Int, productId: Int, fd: Int): Boolean
    external fun detachUSBDevice(deviceId: Int): Boolean
    external fun cleanup()
    
    /**
     * Initialize all native components
     */
    fun initialize(
        winePrefix: String,
        wineArch: String,
        box64LibPath: String
    ): Boolean {
        Timber.d("Initializing native bridge")
        
        // Initialize Wine
        if (!initializeWine(winePrefix, wineArch)) {
            Timber.e("Failed to initialize Wine")
            return false
        }
        
        // Initialize Box64
        if (!initializeBox64(box64LibPath)) {
            Timber.e("Failed to initialize Box64")
            return false
        }
        
        // Initialize USB
        if (!initializeUSB()) {
            Timber.e("Failed to initialize USB")
            return false
        }
        
        Timber.d("Native bridge initialized successfully")
        return true
    }
    
    /**
     * Execute a Windows application
     */
    fun executeApp(
        exePath: String,
        args: String = "",
        workingDir: String = ""
    ): Int {
        Timber.d("Executing app: $exePath")
        return executeWineApp(exePath, args, workingDir)
    }
    
    /**
     * Attach a USB device
     */
    fun attachDevice(vendorId: Int, productId: Int, fd: Int): Boolean {
        Timber.d("Attaching USB device: VID=$vendorId, PID=$productId")
        return attachUSBDevice(vendorId, productId, fd)
    }
    
    /**
     * Detach a USB device
     */
    fun detachDevice(deviceId: Int): Boolean {
        Timber.d("Detaching USB device: $deviceId")
        return detachUSBDevice(deviceId)
    }
    
    /**
     * Cleanup native resources
     */
    fun cleanupResources() {
        Timber.d("Cleaning up native resources")
        cleanup()
    }
}