package com.windroidpro.native_bridge

import com.windroidpro.core.CommandExecutor
import javax.inject.Inject

/**
 * Implementation of CommandExecutor that uses NativeBridge.
 */
class NativeCommandExecutor @Inject constructor() : CommandExecutor {
    override fun execute(exe: String, args: String, workingDir: String): Int {
        return NativeBridge.executeApp(exe, args, workingDir)
    }
}
