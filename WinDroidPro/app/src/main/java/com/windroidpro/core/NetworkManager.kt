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
        val internetSettingsKey = "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings"

        if (container.enableNetworking) {
            Timber.d("Enabling networking (Winsock) for container ${container.name}")
            // Clear any proxy settings that might block connection
            registryManager.setRegistryValue(container, internetSettingsKey, "ProxyEnable", "dword:00000000")
        } else {
            Timber.d("Disabling networking for container ${container.name}")
            // Set a non-existent proxy to effectively kill network
            registryManager.setRegistryValue(container, internetSettingsKey, "ProxyEnable", "dword:00000001")
            registryManager.setRegistryValue(container, internetSettingsKey, "ProxyServer", "127.0.0.1:0")
        }
    }
}
