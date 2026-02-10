package com.windroidpro.core

/**
 * Interface for executing commands in the container environment.
 */
interface CommandExecutor {
    /**
     * Executes an application or command.
     * @param exe The executable path or command name.
     * @param args The arguments to pass.
     * @param workingDir The working directory for the command.
     * @return The exit code.
     */
    fun execute(exe: String, args: String, workingDir: String = ""): Int
}
