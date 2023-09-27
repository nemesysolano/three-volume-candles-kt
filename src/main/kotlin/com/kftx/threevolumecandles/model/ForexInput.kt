package com.kftx.threevolumecandles.model

import java.util.*

data class ForexInput(
    val localDateTime: Date,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val tickVolume: Double,
    val spread: Double,
    val realVolume: Double
) {
    constructor() : this(Date(), 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
}
