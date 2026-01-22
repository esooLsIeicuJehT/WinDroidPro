package com.windroidpro.usb

import timber.log.Timber

/**
 * Low-level USB Manager interfacing with native USB code.
 */
object NativeUsbManager {
    init {
        try {
            // The library "windroidpro" contains both native_bridge and usb_manager code.
            // It might have been loaded by WinDroidApplication or NativeBridge already.
            // But calling loadLibrary again is harmless.
            System.loadLibrary("windroidpro")
            Timber.d("Native library loaded successfully in NativeUsbManager")
        } catch (e: UnsatisfiedLinkError) {
            Timber.e(e, "Failed to load native library in NativeUsbManager")
        }
    }

    external fun nativeOpenDevice(devicePath: String): Int
    external fun nativeCloseDevice(fd: Int)
    external fun nativeReadDevice(fd: Int, buffer: ByteArray, length: Int): Int
    external fun nativeWriteDevice(fd: Int, buffer: ByteArray, length: Int): Int
    external fun nativeControlTransfer(
        fd: Int,
        requestType: Int,
        request: Int,
        value: Int,
        index: Int,
        buffer: ByteArray?,
        length: Int,
        timeout: Int
    ): Int
    external fun nativeBulkTransfer(
        fd: Int,
        endpoint: Int,
        buffer: ByteArray,
        length: Int,
        timeout: Int
    ): Int
    external fun nativeClaimInterface(fd: Int, interfaceNumber: Int): Boolean
    external fun nativeReleaseInterface(fd: Int, interfaceNumber: Int): Boolean
}
