package com.windroidpro.core

import com.windroidpro.data.Container
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegistryManager @Inject constructor() {

    fun applyRegistryPatch(container: Container, patchFile: File) {
        Timber.d("Applying registry patch ${patchFile.name} to container ${container.name}")
        // Ideally we would invoke regedit here, but we can't from here.
        // Instead we place it in a location where a startup script might pick it up.
        // Using standard Windows path case, though filesystem might be case-insensitive or not.
        val startupDir = File(container.prefixPath, "drive_c/windows/Start Menu/Programs/Startup")
        // Try creating if not exists, to be safe
        if (!startupDir.exists()) startupDir.mkdirs()

        if (startupDir.exists()) {
            patchFile.copyTo(File(startupDir, "patch_${System.currentTimeMillis()}.reg"), overwrite = true)
        }
    }

    fun setRegistryKey(container: Container, key: String, value: String) {
        Timber.d("Setting registry key $key = $value for container ${container.name}")
        // Create a temporary reg file and apply it
        try {
            val regContent = """
                Windows Registry Editor Version 5.00

                [$key]
                @="$value"
            """.trimIndent()

            val tempFile = File.createTempFile("update_", ".reg")
            tempFile.writeText(regContent)
            applyRegistryPatch(container, tempFile)
        } catch (e: Exception) {
            Timber.e(e, "Failed to set registry key")
        }
    }

    fun getRegistryKey(container: Container, key: String): String? {
        Timber.d("Reading registry key $key from container ${container.name}")
        return null // Placeholder
    }
}
