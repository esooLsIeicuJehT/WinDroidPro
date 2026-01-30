package com.windroidpro.core

import android.content.Context
import com.windroidpro.data.Container
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import com.windroidpro.utils.ArchiveUtils
import java.io.File
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DxvkManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun installDxvk(container: Container) {
        if (!container.enableDXVK) {
            Timber.d("DXVK disabled for container ${container.name}")
            return
        }

        Timber.d("Installing DXVK for container ${container.name}")
        val system32 = File(container.prefixPath, "drive_c/windows/system32")

        try {
            val dxvkAsset = "dxvk.tar.xz"

            // Check if DXVK is already installed with the same version
            val currentAssetHash = getAssetHash(dxvkAsset)
            val markerFile = File(system32, "dxvk_installed.sha256")

            if (currentAssetHash != null && markerFile.exists()) {
                try {
                    val installedHash = markerFile.readText().trim()
                    if (installedHash == currentAssetHash) {
                        Timber.d("DXVK already installed with matching version ($currentAssetHash). Skipping.")
                        return
                    }
                } catch (e: Exception) {
                    Timber.w("Failed to read existing DXVK marker file")
                }
            }

            // We use a try-catch to allow graceful failure if asset is missing (common in dev)
            try {
                context.assets.open(dxvkAsset).use { inputStream ->
                    ArchiveUtils.extractTarXz(inputStream, system32)
                    Timber.i("DXVK installed to $system32")
                }

                // Write the new hash to the marker file
                if (currentAssetHash != null) {
                    try {
                        markerFile.writeText(currentAssetHash)
                    } catch (e: Exception) {
                        Timber.w("Failed to write DXVK marker file")
                    }
                }
            } catch (e: java.io.FileNotFoundException) {
                Timber.w("DXVK asset not found: $dxvkAsset")
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to install DXVK")
        }
    }

    fun installVkd3d(container: Container) {
        if (!container.enableVKD3D) {
            Timber.d("VKD3D disabled for container ${container.name}")
            return
        }

        Timber.d("Installing VKD3D for container ${container.name}")
        val system32 = File(container.prefixPath, "drive_c/windows/system32")

        try {
            val vkd3dAsset = "vkd3d.tar.xz"
            try {
                context.assets.open(vkd3dAsset).use { inputStream ->
                    ArchiveUtils.extractTarXz(inputStream, system32)
                    Timber.i("VKD3D installed to $system32")
                }
            } catch (e: java.io.FileNotFoundException) {
                Timber.w("VKD3D asset not found: $vkd3dAsset")
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to install VKD3D")
        }
    }

    private fun getAssetHash(assetName: String): String? {
        return try {
            context.assets.open(assetName).use { inputStream ->
                val digest = MessageDigest.getInstance("SHA-256")
                val buffer = ByteArray(8192)
                var bytesRead: Int
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    digest.update(buffer, 0, bytesRead)
                }
                digest.digest().joinToString("") { "%02x".format(it) }
            }
        } catch (e: Exception) {
            Timber.w("Failed to compute hash for asset $assetName: ${e.message}")
            null
        }
    }
}
