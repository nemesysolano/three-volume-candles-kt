package com.kftx.threevolumecandles.feature

import com.kftx.threevolumecandles.model.Direction
import com.kftx.threevolumecandles.model.ScaledCandle
import java.awt.Color
import java.util.stream.IntStream

/**
 * Max Green: RGB(0, 200, 0)
 * Min Green: RGB(0, 100, 0)
 * Max Red: RGB(255, 100, 100)
 * Min Red: RGB(255, 0, 0)
 */
object ColorPicker {
    private val upColorList: Array<Color> = createUpColorList()
    private val downColorList: Array<Color> = createDownColorList()
    private val idleColor = Pair(Color.GRAY, Color.GRAY)
    private val upColor = Pair(Color.BLACK, Color.WHITE)
    private val downColor = Pair(Color.WHITE, Color.BLACK)
    fun forNormalCandle(scaledCandle: ScaledCandle) = if (
        scaledCandle.close > scaledCandle.open
    )
        upColorList[(scaledCandle.tickVolume * 100).toInt()]
    else
        downColorList[(scaledCandle.tickVolume * 100).toInt()]

    fun forBackground(direction: Direction) = when(direction) {
        Direction.IDLE -> idleColor
        Direction.UP -> upColor
        else -> downColor
    }

    private fun createUpColorList() = IntStream.rangeClosed(100, 200).mapToObj { index -> Color(0, index, 0) }.toList().toTypedArray()
    private fun createDownColorList(): Array<Color> = IntStream.rangeClosed(0, 100).mapToObj { index -> Color(255, index, index) }.toList().toTypedArray()
}