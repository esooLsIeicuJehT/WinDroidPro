package com.windroidpro.core

import android.content.Context
import com.windroidpro.data.Container
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import com.windroidpro.utils.ArchiveUtils
import java.io.File
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
            // In a real scenario, check if already installed or version match
            // Assuming dxvk.tar.xz exists in assets
            val dxvkAsset = "dxvk.tar.xz"
            // We use a try-catch to allow graceful failure if asset is missing (common in dev)
            try {
                context.assets.open(dxvkAsset).use { inputStream ->
                    ArchiveUtils.extractTarXz(inputStream, system32)
                    Timber.i("DXVK installed to $system32")
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
}
