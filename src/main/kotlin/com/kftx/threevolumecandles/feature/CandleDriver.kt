package com.kftx.threevolumecandles.feature

import com.kftx.threevolumecandles.LOOKBACK_PERIOD
import com.kftx.threevolumecandles.model.Direction
import com.kftx.threevolumecandles.model.ScaledCandle
import java.util.stream.IntStream
import kotlin.streams.toList

object CandleDriver {

    fun symmetricReversion(array: Array<ScaledCandle>): Array<ScaledCandle> { //
        val startInclusive = 2 * LOOKBACK_PERIOD - 1
        val replica = array.copyOf()
        val endInclusive = array.size
        IntStream.range(startInclusive, endInclusive)
            .parallel().forEach { index -> symmetricReversion(replica[index], replica) }
        return replica
    }

    fun symmetricReversion(scaledCandle: ScaledCandle, array: Array<ScaledCandle>): ScaledCandle {
        val startInclusive = scaledCandle.source.index - 2 * LOOKBACK_PERIOD + 1
        val middlePoint = startInclusive + LOOKBACK_PERIOD - 1
        val endInclusive = middlePoint + LOOKBACK_PERIOD
        val leftEndInclusive = middlePoint - 1
        val rightStartInclusive = middlePoint + 1
        val leftLL = lowestLowIndex(array, startInclusive, leftEndInclusive).first
        val leftHH = highestHighIndex(array, startInclusive, leftEndInclusive).first
        val rightLL = lowestLowIndex(array, rightStartInclusive, endInclusive).first
        val rightHH = highestHighIndex(array, rightStartInclusive, endInclusive).first

        val up = leftHH > array[middlePoint].source.high &&
                array[middlePoint].source.low < rightLL &&
                array[middlePoint].open < array[middlePoint].close &&
                array[middlePoint].close < 0.5
        val down = leftLL < array[middlePoint].source.low &&
                array[middlePoint].source.high > rightHH
        /*
             array[middlePoint].open > array[middlePoint].close
             array[middlePoint].close > 0.2 */
        if(up) {
            array[middlePoint] = array[middlePoint].copy(direction = Direction.UP)
        } else if(down) {
            array[middlePoint] = array[middlePoint].copy(direction = Direction.DOWN)
        }

        return array[middlePoint]
    }

    fun highestHighIndex(array: Array<ScaledCandle>, startInclusive: Int, endInclusive: Int) =
        IntStream.rangeClosed(startInclusive, endInclusive)
            .toList()
            .fold(Pair(array[startInclusive].source.high, startInclusive)) { pair, index ->
                if (array[index].source.high > pair.first) Pair(array[index].source.high, index) else pair
            }

    fun lowestLowIndex(array: Array<ScaledCandle>, startInclusive: Int, endInclusive: Int) =
        IntStream.rangeClosed(startInclusive, endInclusive)
            .toList()
            .fold(Pair(array[startInclusive].source.low, startInclusive)) { pair, index ->
                if (array[index].source.low < pair.first) Pair(array[index].source.low, index) else pair
            }

    fun highestCloseIndex(array: Array<ScaledCandle>, startInclusive: Int, endInclusive: Int) =
        IntStream.rangeClosed(startInclusive, endInclusive)
            .toList()
            .fold(Pair(array[startInclusive].source.close, startInclusive)) { pair, index ->
                if (array[index].source.close > pair.first) Pair(array[index].source.close, index) else pair
            }

    fun lowestCloseIndex(array: Array<ScaledCandle>, startInclusive: Int, endInclusive: Int) =
        IntStream.rangeClosed(startInclusive, endInclusive)
            .toList()
            .fold(Pair(array[startInclusive].source.close, startInclusive)) { pair, index ->
                if (array[index].source.close < pair.first) Pair(array[index].source.close, index) else pair
            }
}