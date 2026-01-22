package com.windroidpro.utils

import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.tukaani.xz.XZInputStream
import timber.log.Timber
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object ArchiveUtils {

    fun extractTarXz(inputStream: InputStream, destinationDir: File) {
        try {
            val xzIn = XZInputStream(BufferedInputStream(inputStream))
            val tarIn = TarArchiveInputStream(xzIn)

            var entry = tarIn.nextTarEntry
            while (entry != null) {
                val outputFile = File(destinationDir, entry.name)
                if (entry.isDirectory) {
                    if (!outputFile.exists()) {
                        outputFile.mkdirs()
                    }
                } else {
                    outputFile.parentFile?.mkdirs()
                    FileOutputStream(outputFile).use { outputStream ->
                        tarIn.copyTo(outputStream)
                    }
                }
                entry = tarIn.nextTarEntry
            }
            tarIn.close()
        } catch (e: Exception) {
            Timber.e(e, "Error extracting tar.xz")
            throw e
        }
    }
}
