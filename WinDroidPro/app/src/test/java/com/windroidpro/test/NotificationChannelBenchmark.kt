package com.windroidpro.test

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.assertTrue
import kotlin.system.measureTimeMillis

class NotificationChannelBenchmark {

    // Simulating the blocking IPC call to create a notification channel
    // In reality this takes 2-20ms+ depending on device load/IPC
    private fun createNotificationChannelBlocking() {
        Thread.sleep(20)
    }

    private suspend fun createNotificationChannelSuspend() {
        delay(20)
    }

    @Test
    fun benchmarkBlockingOnCreate() {
        // Scenario 1: Blocking call in onCreate (Current Implementation)
        val timeTaken = measureTimeMillis {
            // onCreate start
            createNotificationChannelBlocking()
            // onCreate end
        }
        println("Blocking Implementation took: ${timeTaken}ms")
        // It should take at least 20ms
        assertTrue(timeTaken >= 20)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun benchmarkAsyncOnCreate() = runTest {
        // Scenario 2: Async call (Optimized Implementation)

        var initCompletedTime = 0L

        val totalTime = measureTimeMillis {
            // onCreate start
            val job = launch {
                createNotificationChannelSuspend()
            }
            // onCreate "finishes" immediately after launching the job
            initCompletedTime = System.currentTimeMillis()

            // In a real app, onCreate returns here.
            // For the test, we wait for the job to finish to measure total work,
            // but the "user perception" of startup is just the launch overhead.
            job.join()
        }

        // This measurement is a bit tricky in unit tests because virtual time skips delays.
        // However, the principle is that the main thread is not blocked.
        // Let's demonstrate that we can execute "other work" while the channel is being created.

        println("Async test completed.")
    }

    @Test
    fun demonstrateBlockingVsNonBlocking() {
        println("--- Benchmark: Blocking vs Non-Blocking ---")

        // 1. Blocking
        val startBlocking = System.nanoTime()
        createNotificationChannelBlocking() // Blocks for 20ms
        val blockedDuration = (System.nanoTime() - startBlocking) / 1_000_000
        println("Blocking call blocked main thread for: ${blockedDuration}ms")

        // 2. Non-Blocking (Thread simulation)
        val startNonBlocking = System.nanoTime()
        val thread = Thread {
            createNotificationChannelBlocking() // Background work
        }
        thread.start()
        val nonBlockingOverhead = (System.nanoTime() - startNonBlocking) / 1_000_000
        println("Non-blocking dispatch took: ${nonBlockingOverhead}ms (Main thread free to continue)")

        thread.join() // Cleanup

        assertTrue("Non-blocking dispatch should be much faster than the blocking operation", nonBlockingOverhead < blockedDuration)
        println("---------------------------------------------")
    }
}
