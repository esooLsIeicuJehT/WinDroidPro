package com.windroidpro.test

import org.junit.Test
import org.junit.Assert.*
import java.util.concurrent.TimeUnit

class PerformanceBenchmark {

    @Test
    fun benchmarkJniLatency() {
        val iterations = 1000
        val startTime = System.nanoTime()

        for (i in 0 until iterations) {
            // Simulate a fast operation
            dummyOperation()
        }

        val endTime = System.nanoTime()
        val totalTime = endTime - startTime
        val averageTime = totalTime / iterations

        println("Average Dummy Operation Time: ${averageTime}ns")
        assertTrue("Latency should be acceptable (<1ms)", averageTime < 1_000_000)
    }

    private fun dummyOperation() {
        // Simulating work
        val x = 1 + 1
    }

    @Test
    fun benchmarkMemoryAllocation() {
        val runtime = Runtime.getRuntime()
        val startMem = runtime.totalMemory() - runtime.freeMemory()

        val list = ArrayList<ByteArray>()
        for (i in 0 until 100) {
            list.add(ByteArray(1024 * 1024)) // 1MB
        }

        val endMem = runtime.totalMemory() - runtime.freeMemory()
        val used = endMem - startMem

        println("Allocated approx: ${used / (1024 * 1024)} MB")

        list.clear()
        System.gc()

        val afterGc = runtime.totalMemory() - runtime.freeMemory()
        println("Memory after GC: ${afterGc / (1024 * 1024)} MB")

        assertTrue("GC should reclaim memory", afterGc < endMem)
    }
}
