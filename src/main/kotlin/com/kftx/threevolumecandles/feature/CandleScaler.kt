package com.kftx.threevolumecandles.feature

import com.kftx.threevolumecandles.LOOKBACK_PERIOD
import com.kftx.threevolumecandles.model.Candle
import com.kftx.threevolumecandles.model.CandlePlot
import com.kftx.threevolumecandles.model.ScaledCandle
import java.util.*
import java.util.stream.IntStream

object CandleScaler {
    fun h2l2Scaler(array: Array<Candle>): Array<ScaledCandle> = Arrays.stream(array)
        .parallel()
        .map { candle -> h2l2Scaler(candle, array) }
        .toList()
        .toTypedArray()

    private fun h2l2Scaler(candle: Candle, array: Array<Candle>): ScaledCandle = when(candle.index) {
        in 0 until LOOKBACK_PERIOD-1  -> ScaledCandle(candle)
        else -> h2l2Scaler(candle.index-LOOKBACK_PERIOD+1, candle.index+1, array[candle.index], array)
    }

    private fun h2l2Scaler(startInclusive: Int, endExclusive: Int, source: Candle, array: Array<Candle>): ScaledCandle {
        val hh = IntStream.range(startInclusive, endExclusive).mapToDouble{ index -> array[index].high }.max().asDouble
        val ll = IntStream.range(startInclusive, endExclusive).mapToDouble{ index -> array[index].low }.min().asDouble
        val volumeMax = IntStream.range(startInclusive, endExclusive).mapToDouble{ index -> array[index].tickVolume }.max().asDouble

        val scale = hh - ll
        var fit = {x: Double -> (x -ll)/scale}
        return ScaledCandle(
            source,
            fit(source.open),
            fit(source.high),
            fit(source.low),
            fit(source.close),
            source.tickVolume / volumeMax,
        0
        )
    }

    fun plots(array: List<ScaledCandle>): List<CandlePlot> {
        val startInclusive = com.kftx.threevolumecandles.LOOKBACK_PERIOD * 2 - 1
        val endExclusive = array.size
        val result = ArrayList<CandlePlot>(array.size)

//        for(current in startInclusive..endInclusive step BOUNCE_SIZE) {
//            val start = current - BOUNCE_SIZE + 1
//            val slice = array.copyOfRange(start, current + 1)
//            val last = slice.last()
//            result.add(CandlePlot(last.source.localDateTime, slice, last.reversion))
//
//            if(current == 10) {
//                break
//            }
//        }

        return result
    }
}