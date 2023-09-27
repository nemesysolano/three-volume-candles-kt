package com.kftx.threevolumecandles.feature

import com.kftx.threevolumecandles.model.ForexInput
import com.kftx.threevolumecandles.model.ThreeVolumeCandles
import java.util.concurrent.CompletableFuture
import java.util.stream.IntStream
import java.util.stream.Stream
import kotlin.math.pow
import kotlin.math.sqrt

object Extractors {
    val BOUNCE_SIZE = 5

    fun normalizeOpen(array: Array<ThreeVolumeCandles>) {
        val startInclusive = BOUNCE_SIZE - 1
        val endInclusive = array.size - (BOUNCE_SIZE + 1)

        for(current in startInclusive..endInclusive step BOUNCE_SIZE) {
            val start = current - BOUNCE_SIZE + 1
            val max = IntStream.range(start, current+1).mapToDouble { index -> array[index].forexInput.open.toDouble()  }.max().asDouble
            val min = IntStream.range(start, current+1).mapToDouble { index -> array[index].forexInput.open.toDouble()  }.min().asDouble
            val range = max - min

            for(index in start..current) {
                val localRange = (max - array[index].forexInput.open)
                array[index].normalizedOpen = localRange / range
                println()
            }
        }
    }

    fun normalizeHigh(array: Array<ThreeVolumeCandles>) {
        val startInclusive = BOUNCE_SIZE - 1
        val endInclusive = array.size - (BOUNCE_SIZE + 1)

        for(current in startInclusive..endInclusive step BOUNCE_SIZE) {
            val start = current - BOUNCE_SIZE + 1
            val max = IntStream.range(start, current+1).mapToDouble { index -> array[index].forexInput.high.toDouble()  }.max().asDouble
            val min = IntStream.range(start, current+1).mapToDouble { index -> array[index].forexInput.high.toDouble()  }.min().asDouble
            val range = max - min

            for(index in start..current) {
                val localRange = (max - array[index].forexInput.high)
                array[index].normalizedHigh = localRange / range
                println()
            }
        }
    }

    fun normalizeLow(array: Array<ThreeVolumeCandles>) {
        val startInclusive = BOUNCE_SIZE - 1
        val endInclusive = array.size - (BOUNCE_SIZE + 1)

        for(current in startInclusive..endInclusive step BOUNCE_SIZE) {
            val start = current - BOUNCE_SIZE + 1
            val max = IntStream.range(start, current+1).mapToDouble { index -> array[index].forexInput.low.toDouble()  }.max().asDouble
            val min = IntStream.range(start, current+1).mapToDouble { index -> array[index].forexInput.low.toDouble()  }.min().asDouble
            val range = max - min

            for(index in start..current) {
                val localRange = (max - array[index].forexInput.low)
                array[index].normalizedLow = localRange / range
                println()
            }
        }
    }

    fun normalizeClose(array: Array<ThreeVolumeCandles>) {
        val startInclusive = BOUNCE_SIZE - 1
        val endInclusive = array.size - (BOUNCE_SIZE + 1)

        for(current in startInclusive..endInclusive step BOUNCE_SIZE) {
            val start = current - BOUNCE_SIZE + 1
            val max = IntStream.range(start, current+1).mapToDouble { index -> array[index].forexInput.close.toDouble()  }.max().asDouble
            val min = IntStream.range(start, current+1).mapToDouble { index -> array[index].forexInput.close.toDouble()  }.min().asDouble
            val range = max - min

            for(index in start..current) {
                val localRange = (max - array[index].forexInput.close)
                array[index].normalizedClose = localRange / range
                println()
            }
        }
    }

    fun normalizeThickVolume(array: Array<ThreeVolumeCandles>) {
        val startInclusive = BOUNCE_SIZE - 1
        val endInclusive = array.size - (BOUNCE_SIZE + 1)

        for(current in startInclusive..endInclusive step BOUNCE_SIZE) {
            val start = current - BOUNCE_SIZE + 1
            val max = IntStream.range(start, current+1).mapToDouble { index -> array[index].forexInput.tickVolume.toDouble()  }.max().asDouble
            val min = IntStream.range(start, current+1).mapToDouble { index -> array[index].forexInput.tickVolume.toDouble()  }.min().asDouble
            val range = max - min

            for(index in start..current) {
                val localRange = (max - array[index].forexInput.tickVolume)
                array[index].normalizedTickVolume = localRange / range
                println()
            }
        }
    }

    fun detectReversion(array: Array<ThreeVolumeCandles>) {
        val reversionBounceSize = BOUNCE_SIZE * 2
        val startInclusive = BOUNCE_SIZE - 1
        val endInclusive = array.size - (reversionBounceSize + 1)

        for(current in startInclusive..endInclusive step BOUNCE_SIZE) {
            val currentHigh = array[current].forexInput.high
            val currentLow = array[current].forexInput.low
            val targetHigh = array[current + (reversionBounceSize + 1)].forexInput.high
            val targetLow = array[current + (reversionBounceSize + 1)].forexInput.low

            array[current].reversion = if(
                currentHigh < targetLow
            ) 1 else if(
                currentLow > targetHigh
            ) -1 else 0

        }
    }
}