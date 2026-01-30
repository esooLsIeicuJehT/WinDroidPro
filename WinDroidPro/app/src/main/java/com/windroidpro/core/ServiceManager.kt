package com.windroidpro.core

import com.windroidpro.data.Container
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServiceManager @Inject constructor(
    private val commandExecutor: CommandExecutor
) {

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
        val escapedName = escapeArg(serviceName)
        val workingDir = java.io.File(container.prefixPath, "drive_c/windows/system32").absolutePath
        val result = commandExecutor.execute("net", "start \"$escapedName\"", workingDir)
        if (result != 0) {
            Timber.e("Failed to start service $serviceName (code $result)")
        }
    }

    fun stopService(container: Container, serviceName: String) {
        Timber.d("Stopping service $serviceName in container ${container.name}")
        val escapedName = escapeArg(serviceName)
        val workingDir = java.io.File(container.prefixPath, "drive_c/windows/system32").absolutePath
        val result = commandExecutor.execute("net", "stop \"$escapedName\"", workingDir)
        if (result != 0) {
            Timber.e("Failed to stop service $serviceName (code $result)")
        }
    }

    private fun escapeArg(arg: String): String {
        return arg.replace("\"", "\\\"")
    }
}
