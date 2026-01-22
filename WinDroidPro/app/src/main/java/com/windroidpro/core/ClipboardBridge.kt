package com.windroidpro.core

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.windroidpro.data.Container
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClipboardBridge @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    private var isListening = false
    private var currentContainer: Container? = null

    private val clipboardListener = ClipboardManager.OnPrimaryClipChangedListener {
        val clip = clipboardManager.primaryClip
        if (clip != null && clip.itemCount > 0) {
            val text = clip.getItemAt(0).text?.toString() ?: ""
            Timber.d("Android clipboard changed: $text")

            // Write to clipboard_in.txt in the container's user directory
            currentContainer?.let { container ->
                 try {
                     // Assuming 'public' user or shared location
                     val clipboardFile = File(container.prefixPath, "drive_c/users/Public/clipboard_in.txt")
                     clipboardFile.parentFile?.mkdirs()
                     clipboardFile.writeText(text)
                 } catch (e: Exception) {
                     Timber.e(e, "Failed to write clipboard data")
                 }
            }
        }
    }

    fun startListening(container: Container) {
        if (container.enableClipboardSharing && !isListening) {
            Timber.d("Starting clipboard bridge for container ${container.name}")
            currentContainer = container
            clipboardManager.addPrimaryClipChangedListener(clipboardListener)
            isListening = true
        }
    }

    fun stopListening() {
        if (isListening) {
            Timber.d("Stopping clipboard bridge")
            clipboardManager.removePrimaryClipChangedListener(clipboardListener)
            isListening = false
        }
    }

    fun updateAndroidClipboard(text: String) {
        Timber.d("Updating Android clipboard from Wine: $text")
        val clip = ClipData.newPlainText("WinDroid", text)
        clipboardManager.setPrimaryClip(clip)
    }
}
