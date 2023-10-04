package com.kftx.threevolumecandles.feature

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

object Mathematics {
    private const val MAX_SCALE = 5
     fun mantissaScale(a: Double, b: Double): Int {
        var x = a
        var y = b
        var delta = abs(a - b).toInt()
        var scale = 1
        var index = 0

        while(delta == 0 && index < MAX_SCALE) {
            x = (x - x.toInt())*10
            y = (y - y.toInt())*10
            scale *= 10
            delta = abs(x - y).toInt()
            index += 1
        }
        return scale
    }

    fun mantissa(number: Double, scale: Int) = if(scale < 1) number else number*(scale/10) - (number*(scale/10)).toInt()
}