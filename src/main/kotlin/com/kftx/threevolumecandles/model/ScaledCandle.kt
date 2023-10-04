package com.kftx.threevolumecandles.model

data class ScaledCandle(
    val source: Candle,
    var open: Double = 0.0,
    val high: Double = 0.0,
    val low: Double = 0.0,
    val close: Double = 0.0,
    val tickVolume: Double = 0.0,
    val direction: Int = 0

) {
    companion object {
        fun fromCandle(candle: Candle) = ScaledCandle(
            candle
        )
    }
}
