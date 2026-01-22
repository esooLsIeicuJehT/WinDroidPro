package com.windroidpro.core

import com.windroidpro.data.Container
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServiceManager @Inject constructor() {

    fun startServices(container: Container) {
        if (!container.enableServices) {
            Timber.d("Services disabled for container ${container.name}")
            return
        }

        try {
            val startupDir = java.io.File(container.prefixPath, "drive_c/windows/Start Menu/Programs/Startup")
            if (!startupDir.exists()) startupDir.mkdirs()

            val batchFile = java.io.File(startupDir, "services_startup.bat")
            val content = StringBuilder("@echo off\r\n")

            container.servicesList.forEach { service ->
                content.append("net start \"$service\"\r\n")
            }

            batchFile.writeText(content.toString())
            Timber.i("Created service startup script for ${container.name}: ${batchFile.absolutePath}")
        } catch (e: Exception) {
            Timber.e(e, "Failed to create service startup script")
        }
    }

    fun startService(container: Container, serviceName: String) {
        Timber.d("Starting service $serviceName in container ${container.name}")
        // Runtime start not implemented yet, relies on startup script
    }

    fun stopService(container: Container, serviceName: String) {
        Timber.d("Stopping service $serviceName in container ${container.name}")
        // Runtime stop not implemented yet
    }
}
