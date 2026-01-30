package com.windroidpro.core

import com.windroidpro.data.Container
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ServiceManagerTest {

    private lateinit var serviceManager: ServiceManager
    private lateinit var fakeExecutor: FakeCommandExecutor

    @Before
    fun setup() {
        fakeExecutor = FakeCommandExecutor()
        serviceManager = ServiceManager(fakeExecutor)
    }

    @Test
    fun stopService_executesNetStop() {
        // Create a dummy container
        val container = Container(
            name = "TestContainer",
            prefixPath = "/tmp/test"
        )
        serviceManager.stopService(container, "MyService")

        assertEquals("net", fakeExecutor.lastExe)
        assertEquals("stop \"MyService\"", fakeExecutor.lastArgs)
        assertEquals("/tmp/test/drive_c/windows/system32", fakeExecutor.lastWorkingDir)
    }

    @Test
    fun startService_executesNetStart() {
        // Create a dummy container
        val container = Container(
            name = "TestContainer",
            prefixPath = "/tmp/test"
        )
        serviceManager.startService(container, "MyService")

        assertEquals("net", fakeExecutor.lastExe)
        assertEquals("start \"MyService\"", fakeExecutor.lastArgs)
        assertEquals("/tmp/test/drive_c/windows/system32", fakeExecutor.lastWorkingDir)
    }

    @Test
    fun stopService_escapesQuotes() {
        // Create a dummy container
        val container = Container(
            name = "TestContainer",
            prefixPath = "/tmp/test"
        )
        serviceManager.stopService(container, "My \"Service\"")

        assertEquals("net", fakeExecutor.lastExe)
        assertEquals("stop \"My \\\"Service\\\"\"", fakeExecutor.lastArgs)
    }

    class FakeCommandExecutor : CommandExecutor {
        var lastExe: String? = null
        var lastArgs: String? = null
        var lastWorkingDir: String? = null
        var returnCode: Int = 0

        override fun execute(exe: String, args: String, workingDir: String): Int {
            lastExe = exe
            lastArgs = args
            lastWorkingDir = workingDir
            return returnCode
        }
    }
}
