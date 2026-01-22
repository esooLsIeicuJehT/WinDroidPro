package com.windroidpro.usb

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Low-level USB Manager interfacing with native USB code.
 */
object NativeUsbManager {
    private val _isLibraryLoaded = MutableStateFlow(false)
    val isLibraryLoaded: StateFlow<Boolean> = _isLibraryLoaded

    fun loadLibraryAsync() {
        if (_isLibraryLoaded.value) {
            Timber.d("Native library already loaded.")
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                System.loadLibrary("windroidpro")
                _isLibraryLoaded.value = true
                Timber.d("Native library loaded successfully.")
            } catch (e: UnsatisfiedLinkError) {
                Timber.e(e, "Failed to load native library.")
                _isLibraryLoaded.value = false
            }
        }
    }

    external fun nativeOpenDevice(devicePath: String): Int
    external fun nativeCloseDevice(fd: Int)
    external fun nativeReadDevice(fd: Int, buffer: ByteArray, length: Int): Int
    external fun nativeWriteDevice(fd: Int, buffer: ByteArray, length: Int): Int

    private suspend fun awaitLibraryLoad() {
        if (!isLibraryLoaded.value) {
            Timber.d("Waiting for native library to load...")
            isLibraryLoaded.first { it }
            Timber.d("Native library loaded.")
        }
    }

    suspend fun openDevice(devicePath: String): Int = withContext(Dispatchers.IO) {
        awaitLibraryLoad()
        nativeOpenDevice(devicePath)
    }

    suspend fun closeDevice(fd: Int) = withContext(Dispatchers.IO) {
        awaitLibraryLoad()
        nativeCloseDevice(fd)
    }

    suspend fun readDevice(fd: Int, buffer: ByteArray, length: Int): Int = withContext(Dispatchers.IO) {
        awaitLibraryLoad()
        nativeReadDevice(fd, buffer, length)
    }

    suspend fun writeDevice(fd: Int, buffer: ByteArray, length: Int): Int = withContext(Dispatchers.IO) {
        awaitLibraryLoad()
        nativeWriteDevice(fd, buffer, length)
    }

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

    suspend fun controlTransfer(
        fd: Int,
        requestType: Int,
        request: Int,
        value: Int,
        index: Int,
        buffer: ByteArray?,
        length: Int,
        timeout: Int
    ): Int = withContext(Dispatchers.IO) {
        awaitLibraryLoad()
        nativeControlTransfer(fd, requestType, request, value, index, buffer, length, timeout)
    }

    suspend fun bulkTransfer(
        fd: Int,
        endpoint: Int,
        buffer: ByteArray,
        length: Int,
        timeout: Int
    ): Int = withContext(Dispatchers.IO) {
        awaitLibraryLoad()
        nativeBulkTransfer(fd, endpoint, buffer, length, timeout)
    }

    suspend fun claimInterface(fd: Int, interfaceNumber: Int): Boolean = withContext(Dispatchers.IO) {
        awaitLibraryLoad()
        nativeClaimInterface(fd, interfaceNumber)
    }

    suspend fun releaseInterface(fd: Int, interfaceNumber: Int): Boolean = withContext(Dispatchers.IO) {
        awaitLibraryLoad()
        nativeReleaseInterface(fd, interfaceNumber)
    }
}
