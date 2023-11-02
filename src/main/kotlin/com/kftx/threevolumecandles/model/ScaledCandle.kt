package com.kftx.threevolumecandles.model

data class ScaledCandle(
    val source: Candle,
    val index: Int,
    var open: Double = 0.0,
    val high: Double = 0.0,
    val low: Double = 0.0,
    val close: Double = 0.0,
    val tickVolume: Double = 0.0,
    val direction: Direction = Direction.IDLE,
    val stopLoss: Double = 0.0,
    val takeProfit: Double = 0.0
) {
    companion object {
        fun fromCandle(candle: Candle, index: Int) = ScaledCandle(
            candle,
            index
        )
    }
}
