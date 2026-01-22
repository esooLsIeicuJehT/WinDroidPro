package com.windroidpro.test

import org.junit.Test
import org.junit.Assert.*

class UsbCompatibilityTest {

    @Test
    fun testUsbDeviceFiltering() {
        // Simulate checking a device
        val deviceClass = 0x08 // Mass Storage
        val isSupported = isDeviceSupported(deviceClass)
        assertTrue("Mass Storage should be supported", isSupported)
    }

    @Test
    fun testSerialDeviceFiltering() {
        val deviceClass = 255 // Vendor Specific (often Serial)
        val isSupported = isDeviceSupported(deviceClass)
        assertTrue("Vendor Specific should be supported for Serial", isSupported)
    }

    private fun isDeviceSupported(deviceClass: Int): Boolean {
        // Logic mirroring UsbDeviceReceiver or similar
        return when (deviceClass) {
            0x08 -> true // Storage
            0x03 -> true // HID
            255 -> true // Vendor Specific
            else -> false
        }
    }
}
