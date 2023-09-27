package com.kftx.threevolumecandles.model

data class ThreeVolumeCandles(
    val forexInput: ForexInput,
    var normalizedOpen: Double = 0.0,
    var normalizedHigh: Double = 0.0,
    var normalizedLow: Double = 0.0,
    var normalizedClose: Double = 0.0,
    var normalizedTickVolume: Double = 0.0,
    var reversion: Int = 0

) {
    companion object {
        fun fromForexInput(forexInput: ForexInput) = ThreeVolumeCandles(
            forexInput
        )
    }
}
