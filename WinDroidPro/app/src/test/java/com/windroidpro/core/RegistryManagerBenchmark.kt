package com.windroidpro.core

import com.windroidpro.data.Container
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.util.concurrent.TimeUnit

class RegistryManagerBenchmark {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private val registryManager = RegistryManager()

    @Test
    fun benchmarkApplyRegistryPatch() = runBlocking {
        val containerDir = tempFolder.newFolder("container")
        val container = Container(name = "Test", prefixPath = containerDir.absolutePath)
        val patchFile = tempFolder.newFile("test.reg")
        patchFile.writeText("Windows Registry Editor Version 5.00\n[HKEY_CURRENT_USER\\Software\\Test]\n\"Value\"=\"Test\"")

        val iterations = 100
        val startTime = System.nanoTime()

        repeat(iterations) {
            registryManager.applyRegistryPatch(container, patchFile)
        }

        val endTime = System.nanoTime()
        val duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime)
        println("applyRegistryPatch baseline ($iterations iterations): ${duration}ms")
    }

    @Test
    fun benchmarkSetRegistryValue() = runBlocking {
        val containerDir = tempFolder.newFolder("container_set")
        val container = Container(name = "Test", prefixPath = containerDir.absolutePath)

        val iterations = 100
        val startTime = System.nanoTime()

        repeat(iterations) { i ->
            registryManager.setRegistryValue(container, "HKEY_CURRENT_USER\\Software\\Test", "Value$i", "Value$i")
        }

        val endTime = System.nanoTime()
        val duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime)
        println("setRegistryValue baseline ($iterations iterations): ${duration}ms")
    }
}
