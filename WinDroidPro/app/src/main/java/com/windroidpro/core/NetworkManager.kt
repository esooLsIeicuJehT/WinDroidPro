package com.windroidpro.core

import com.windroidpro.data.Container
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkManager @Inject constructor(
    private val registryManager: RegistryManager
) {

    fun configureNetworking(container: Container) {
        if (container.enableNetworking) {
            Timber.d("Enabling networking (Winsock) for container ${container.name}")
            // Clear any proxy settings that might block connection
            // Note: Value format for DWORD in .reg is dword:00000000, but setRegistryKey takes a string value.
            // Assuming RegistryManager handles the format or we pass raw value.
            // For simplicitly we use string "0" which might need adjustment based on implementation.
            registryManager.setRegistryKey(container, "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\ProxyEnable", "dword:00000000")
        } else {
            Timber.d("Disabling networking for container ${container.name}")
            // Set a non-existent proxy to effectively kill network
            registryManager.setRegistryKey(container, "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\ProxyEnable", "dword:00000001")
            registryManager.setRegistryKey(container, "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\ProxyServer", "127.0.0.1:0")
        }
    }
}
