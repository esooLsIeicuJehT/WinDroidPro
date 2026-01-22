package com.windroidpro.core

import com.windroidpro.data.Container
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WslManager @Inject constructor() {

    fun configureWsl(container: Container) {
        if (container.enableWSL) {
            Timber.d("Enabling WSL mode for container ${container.name}")
            // Create a wsl.bat in system32 to simulate WSL command
            val system32 = java.io.File(container.prefixPath, "drive_c/windows/system32")
            if (system32.exists()) {
                val wslBat = java.io.File(system32, "wsl.bat")
                try {
                    // Simple simulation that launches cmd.exe as if it were a shell
                    wslBat.writeText("@echo off\r\ncmd.exe /k \"echo WSL simulation mode\"")
                } catch (e: Exception) {
                    Timber.e(e, "Failed to create WSL shim")
                }
            }
        }
    }

    fun launchWslShell(container: Container) {
        Timber.d("Launching WSL shell for container ${container.name}")
        // Execute bash inside the container environment
    }
}
