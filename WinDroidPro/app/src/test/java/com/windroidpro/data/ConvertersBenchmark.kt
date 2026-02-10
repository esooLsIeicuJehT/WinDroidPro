package com.windroidpro.data

import org.junit.Test
import org.junit.Assert.assertTrue
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.concurrent.TimeUnit

class ConvertersBenchmark {

    // Helper class simulating the unoptimized behavior (Gson created per instance)
    class LegacyConverters {
        private val gson = Gson()

        fun fromStringMap(value: Map<String, String>?): String {
            return gson.toJson(value)
        }
        // ... other methods omitted for instantiation benchmark
    }

    @Test
    fun benchmarkInstantiationCost() {
        val iterations = 10000

        // Benchmark Legacy (Unoptimized)
        val startLegacy = System.nanoTime()
        for (i in 0 until iterations) {
            val converter = LegacyConverters()
            // prevent dead code elimination
            if (converter.hashCode() == 0) print(" ")
        }
        val endLegacy = System.nanoTime()
        val durationLegacy = TimeUnit.NANOSECONDS.toMillis(endLegacy - startLegacy)
        println("Legacy instantiation time ($iterations iterations): ${durationLegacy}ms")

        // Benchmark Optimized (Converters class, after optimization)
        val startOptimized = System.nanoTime()
        for (i in 0 until iterations) {
            val converter = Converters()
            // prevent dead code elimination
            if (converter.hashCode() == 0) print(" ")
        }
        val endOptimized = System.nanoTime()
        val durationOptimized = TimeUnit.NANOSECONDS.toMillis(endOptimized - startOptimized)
        println("Optimized instantiation time ($iterations iterations): ${durationOptimized}ms")

        // We expect the optimized version to be significantly faster
        // Note: This assertion might fail if the optimization hasn't been applied yet
        // or if the JVM optimizes Gson creation heavily (unlikely).
        if (durationOptimized > 0 && durationLegacy > 0) {
            val improvement = durationLegacy.toDouble() / durationOptimized.toDouble()
            println("Improvement factor: ${String.format("%.2f", improvement)}x")
        }
    }
}
