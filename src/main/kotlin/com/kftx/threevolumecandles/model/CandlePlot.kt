package com.kftx.threevolumecandles.model

import com.kftx.threevolumecandles.LOOKBACK_PERIOD
import com.kftx.threevolumecandles.feature.ColorPicker
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.util.*
import javax.imageio.ImageIO
import kotlin.math.abs
// [Working with Images in Java](https://www.baeldung.com/java-images)
data class CandlePlot(
    val localDateTime: Date,
    val finalCandle: ScaledCandle,
    val direction: Int,
    val image: BufferedImage
) {
    constructor(localDateTime: Date, sourceCandles: Array<ScaledCandle>, direction: Int): this(
        localDateTime,
        sourceCandles[LOOKBACK_PERIOD-1],
        direction,
        ImageIO.read(CandlePlot::class.java.getResourceAsStream("/CandlePlotTemplate.png"))
    ) {

        val witdh = 250 // pixels
        val height = 250 // pixels
        val graphics = image.graphics as Graphics2D
        val candleWidth = 25
        val horizGap = 3 //pixels
        var horizOffset = 3

        sourceCandles.forEach { candle ->
            val color = ColorPicker.forNormalCandle(candle)
            graphics.color = color

            graphics.fillRect(
                horizOffset,
                (candle.open.coerceAtMost(candle.close) * height).toInt(),
                candleWidth,
                (abs(candle.open - candle.close) *height).toInt()
            )

            horizOffset += candleWidth + horizGap
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CandlePlot

        if (localDateTime != other.localDateTime) return false
        if (finalCandle != other.finalCandle) return false
        if (direction != other.direction) return false
        return image == other.image
    }

    override fun hashCode(): Int {
        var result = localDateTime.hashCode()
        result = 31 * result + finalCandle.hashCode()
        result = 41 * result + direction
        result = 57 * result + image.hashCode()
        return result
    }

    companion object {

    }
}