package com.windroidpro.core

import com.windroidpro.data.Container
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegistryManager @Inject constructor() {

    suspend fun applyRegistryPatch(container: Container, patchFile: File) = withContext(Dispatchers.IO) {
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
    private fun generateRegFragment(keyPath: String, valueName: String, value: String): String {
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

        return "\n[$keyPath]\n$finalValueName=$finalValue\n"
    }

    /**
     * Generates the content of a .reg file.
     * Visible for testing.
     */
    internal fun generateRegContent(keyPath: String, valueName: String, value: String): String {
        return "Windows Registry Editor Version 5.00\n" + generateRegFragment(keyPath, valueName, value)
    }

    suspend fun setRegistryValue(container: Container, keyPath: String, valueName: String, value: String) = withContext(Dispatchers.IO) {
        Timber.d("Setting registry value $keyPath\\$valueName = $value for container ${container.name}")
        try {
            val startupDir = File(container.prefixPath, "drive_c/windows/Start Menu/Programs/Startup")
            if (!startupDir.exists()) startupDir.mkdirs()

            val updateFile = File(startupDir, "user_updates.reg")

            synchronized(this@RegistryManager) {
                if (!updateFile.exists()) {
                    updateFile.writeText("Windows Registry Editor Version 5.00\n")
                }
                updateFile.appendText(generateRegFragment(keyPath, valueName, value))
            }
            Timber.d("Appended registry value to ${updateFile.name}")
        } catch (e: Exception) {
            Timber.e(e, "Failed to set registry value")
        }
    }

    suspend fun getRegistryValue(container: Container, keyPath: String, valueName: String): String? = withContext(Dispatchers.IO) {
        val (hive, subKey) = splitHiveAndSubKey(keyPath) ?: return@withContext null
        val regFileName = when (hive.uppercase()) {
            "HKEY_LOCAL_MACHINE", "HKLM" -> "system.reg"
            "HKEY_CURRENT_USER", "HKCU" -> "user.reg"
            else -> return@withContext null
        }

        val regFile = File(container.prefixPath, regFileName)
        if (!regFile.exists()) {
            Timber.d("Registry file not found: ${regFile.absolutePath}")
            return@withContext null
        }

        // Clean subKey (remove trailing slashes) and convert to Wine format
        val cleanSubKey = subKey.trimEnd('\\')
        val wineSubKey = cleanSubKey.replace("\\", "\\\\")
        val sectionHeaderStart = "[$wineSubKey]"

        val targetPrefix = if (valueName.isEmpty() || valueName == "@") "@=" else "\"$valueName\"="

        try {
            regFile.useLines { lines ->
                var insideTargetSection = false
                for (line in lines) {
                    val trimmed = line.trim()
                    if (trimmed.startsWith("[")) {
                        // Check if we are entering the target section
                        // Wine sections often have timestamp at the end: [Software\\Test] 123123
                        // We check if it starts with [Software\\Test]
                        if (trimmed.startsWith(sectionHeaderStart, ignoreCase = true)) {
                            insideTargetSection = true
                        } else {
                            if (insideTargetSection) return@useLines null // Left the section
                            insideTargetSection = false
                        }
                    } else if (insideTargetSection) {
                        if (trimmed.startsWith(targetPrefix, ignoreCase = true)) {
                            val rawValue = trimmed.substring(targetPrefix.length)

                            // Parse value
                            if (rawValue.startsWith("\"") && rawValue.endsWith("\"")) {
                                // String value: Unescape
                                val content = rawValue.substring(1, rawValue.length - 1)
                                return@useLines unescapeRegistryString(content)
                            } else {
                                // dword, hex, or other
                                return@useLines rawValue
                            }
                        }
                    }
                }
                null
            }
        } catch (e: Exception) {
            Timber.e(e, "Error reading registry file")
            null
        }
    }

    private fun splitHiveAndSubKey(keyPath: String): Pair<String, String>? {
        val parts = keyPath.split('\\', limit = 2)
        if (parts.size < 2) return null
        return parts[0] to parts[1]
    }

    private fun unescapeRegistryString(value: String): String {
        val sb = StringBuilder()
        var i = 0
        while (i < value.length) {
            val c = value[i]
            if (c == '\\' && i + 1 < value.length) {
                val next = value[i + 1]
                if (next == '\\') {
                    sb.append('\\')
                    i += 2
                } else if (next == '"') {
                    sb.append('"')
                    i += 2
                } else {
                    sb.append(c)
                    i++
                }
            } else {
                sb.append(c)
                i++
            }
        }
        return sb.toString()
    }
}
