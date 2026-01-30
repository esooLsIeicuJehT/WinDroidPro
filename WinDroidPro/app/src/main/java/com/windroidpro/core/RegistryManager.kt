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

    /**
     * Generates the content of a .reg file.
     * Visible for testing.
     */
    internal fun generateRegContent(keyPath: String, valueName: String, value: String): String {
        val finalValueName = if (valueName.isEmpty() || valueName == "@") "@" else "\"$valueName\""

        // Determine if value needs quotes
        // dword: and hex: values are not quoted
        val finalValue = if (value.startsWith("dword:") || value.startsWith("hex:")) {
            value
        } else {
            // Escape quotes and backslashes for string values
            val escapedValue = value.replace("\\", "\\\\").replace("\"", "\\\"")
            "\"$escapedValue\""
        }

        return """
            Windows Registry Editor Version 5.00

            [$keyPath]
            $finalValueName=$finalValue
        """.trimIndent()
    }

    fun setRegistryValue(container: Container, keyPath: String, valueName: String, value: String) {
        Timber.d("Setting registry value $keyPath\\$valueName = $value for container ${container.name}")
        try {
            val regContent = generateRegContent(keyPath, valueName, value)
            val tempFile = File.createTempFile("update_", ".reg")
            tempFile.writeText(regContent)
            applyRegistryPatch(container, tempFile)
        } catch (e: Exception) {
            Timber.e(e, "Failed to set registry value")
        }
    }

    fun getRegistryKey(container: Container, key: String): String? {
        Timber.d("Reading registry key $key from container ${container.name}")
        return null // Placeholder
    }
}
