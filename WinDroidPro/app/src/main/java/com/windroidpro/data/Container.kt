package com.windroidpro.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Represents a Wine container (Windows environment)
 */
@Entity(tableName = "containers")
data class Container(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val wineVersion: String = "9.0",
    val architecture: String = "win64", // win32 or win64
    val prefixPath: String,
    val createdAt: Long = System.currentTimeMillis(),
    val lastUsed: Long = System.currentTimeMillis(),
    val isRunning: Boolean = false,
    
    // Configuration
    val screenWidth: Int = 1280,
    val screenHeight: Int = 720,
    val dpi: Int = 96,
    val enableDXVK: Boolean = true,
    val enableVKD3D: Boolean = true,
    val enableBox64: Boolean = true,
    val box64Preset: String = "performance", // performance, balanced, stability
    
    // Environment variables
    val environmentVariables: Map<String, String> = emptyMap(),
    
    // USB devices
    val attachedUsbDevices: List<String> = emptyList()
)

/**
 * Container with installed applications
 */
data class ContainerWithApps(
    val container: Container,
    val applications: List<WindowsApplication>
)

/**
 * Represents a Windows application
 */
@Entity(tableName = "applications")
data class WindowsApplication(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val containerId: String,
    val name: String,
    val exePath: String,
    val workingDirectory: String = "",
    val arguments: String = "",
    val iconPath: String? = null,
    val lastUsed: Long = System.currentTimeMillis()
)

/**
 * USB device information
 */
data class UsbDevice(
    val vendorId: Int,
    val productId: Int,
    val deviceName: String,
    val manufacturer: String?,
    val serialNumber: String?,
    val deviceClass: Int,
    val deviceSubclass: Int,
    val isAttached: Boolean = false
)

/**
 * System information
 */
data class SystemInfo(
    val cpuModel: String,
    val cpuCores: Int,
    val totalMemory: Long,
    val availableMemory: Long,
    val androidVersion: String,
    val kernelVersion: String,
    val hasVulkanSupport: Boolean,
    val hasUsbOtgSupport: Boolean
)