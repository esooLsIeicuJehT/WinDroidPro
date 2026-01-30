package com.windroidpro.core

import org.junit.Assert.assertEquals
import org.junit.Test

class RegistryManagerTest {

    private val registryManager = RegistryManager()

    @Test
    fun generateRegContent_handlesDword() {
        val content = registryManager.generateRegContent(
            "HKEY_CURRENT_USER\\Software\\Test",
            "TestDword",
            "dword:00000001"
        )

        val expected = """
            Windows Registry Editor Version 5.00

            [HKEY_CURRENT_USER\Software\Test]
            "TestDword"=dword:00000001
        """.trimIndent()

        assertEquals(expected, content)
    }

    @Test
    fun generateRegContent_handlesString() {
        val content = registryManager.generateRegContent(
            "HKEY_CURRENT_USER\\Software\\Test",
            "TestString",
            "Hello World"
        )

        val expected = """
            Windows Registry Editor Version 5.00

            [HKEY_CURRENT_USER\Software\Test]
            "TestString"="Hello World"
        """.trimIndent()

        assertEquals(expected, content)
    }

    @Test
    fun generateRegContent_handlesEscaping() {
        val content = registryManager.generateRegContent(
            "HKEY_CURRENT_USER\\Software\\Test",
            "TestPath",
            "C:\\Windows\\System32"
        )

        val expected = """
            Windows Registry Editor Version 5.00

            [HKEY_CURRENT_USER\Software\Test]
            "TestPath"="C:\\Windows\\System32"
        """.trimIndent()

        assertEquals(expected, content)
    }

    @Test
    fun generateRegContent_handlesDefaultValue() {
        val content = registryManager.generateRegContent(
            "HKEY_CURRENT_USER\\Software\\Test",
            "@",
            "DefaultValue"
        )

        val expected = """
            Windows Registry Editor Version 5.00

            [HKEY_CURRENT_USER\Software\Test]
            @="DefaultValue"
        """.trimIndent()

        assertEquals(expected, content)
    }

    @Test
    fun generateRegContent_handlesEmptyValueNameAsDefault() {
        val content = registryManager.generateRegContent(
            "HKEY_CURRENT_USER\\Software\\Test",
            "",
            "DefaultValue"
        )

        val expected = """
            Windows Registry Editor Version 5.00

            [HKEY_CURRENT_USER\Software\Test]
            @="DefaultValue"
        """.trimIndent()

        assertEquals(expected, content)
    }

    @Test
    fun generateRegContent_handlesHex() {
        val content = registryManager.generateRegContent(
            "HKEY_CURRENT_USER\\Software\\Test",
            "BinaryData",
            "hex:01,02,03,04"
        )

        val expected = """
            Windows Registry Editor Version 5.00

            [HKEY_CURRENT_USER\Software\Test]
            "BinaryData"=hex:01,02,03,04
        """.trimIndent()

        assertEquals(expected, content)
    }
}
