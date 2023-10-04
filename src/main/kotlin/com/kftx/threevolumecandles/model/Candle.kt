package com.kftx.threevolumecandles.model

import java.util.*

data class Candle(
    val index: Int,
    val localDateTime: Date,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val tickVolume: Double,
    val spread: Double,
    val realVolume: Double
) {
    constructor() : this(-1, Date(), 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
    constructor(
        index: Int,
        open: Double,
        high: Double,
        low: Double,
        close: Double,
    ) : this(index, Date(), open, high, low, close, 0.0, 0.0, 0.0)
}
