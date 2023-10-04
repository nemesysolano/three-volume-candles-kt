package com.kftx.threevolumecandles.feature

import com.kftx.threevolumecandles.LOOKBACK_PERIOD
import com.kftx.threevolumecandles.model.ScaledCandle
import java.util.stream.IntStream
import kotlin.streams.toList

object CandleDriver {

    fun symmetricReversion(array: Array<ScaledCandle>): Array<ScaledCandle> {
        val startInclusive = 2 * LOOKBACK_PERIOD - 1
        val endInclusive = array.size
        return IntStream.range(startInclusive, endInclusive)
            /*.parallel()*/.mapToObj { index -> symmetricReversion(array[index], array) }.toList().toTypedArray()
    }

    fun symmetricReversion(scaledCandle: ScaledCandle, array: Array<ScaledCandle>): ScaledCandle {
        val startInclusive = scaledCandle.source.index - 2 * LOOKBACK_PERIOD + 1
        val middlePoint = startInclusive + LOOKBACK_PERIOD - 1
        val endInclusive = middlePoint + LOOKBACK_PERIOD

        val rightLC = lowestCloseIndex(array, middlePoint + 1, endInclusive)
        val rightHC = highestCloseIndex(array, middlePoint + 1, endInclusive)
        val up =  rightHC.first  >  array[middlePoint].source.close
        val down =  rightLC.first <  array[middlePoint].source.close
        val result = if(up && !down) {
            scaledCandle.copy(direction = 1)
        } else if(!up && down) {
            scaledCandle.copy(direction = -1)
        } else scaledCandle

        return result
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