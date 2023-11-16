package com.kftx.threevolumecandles.io

import com.kftx.threevolumecandles.LOOKBACK_PERIOD
import com.kftx.threevolumecandles.feature.ColorPicker
import com.kftx.threevolumecandles.feature.PNGGenerationContext
import com.kftx.threevolumecandles.feature.PNGGenerationContextList
import com.kftx.threevolumecandles.model.Direction
import com.kftx.threevolumecandles.model.ScaledCandle
import java.awt.Color
import java.awt.Graphics2D
import java.awt.geom.Line2D
import java.awt.geom.Rectangle2D
import java.io.File
import java.util.stream.IntStream
import javax.imageio.ImageIO
import kotlin.math.abs


object PNG {
    private const val width = 100
    private const val height = 100
    fun writePlot(bufferedImageList: PNGGenerationContextList, directory: File, array: Array<ScaledCandle>, index: Int) {
        val startInclusive = index - LOOKBACK_PERIOD + 1
        val pngGenerationContext = bufferedImageList.take()
        val slice = array.slice(IntRange(startInclusive, index))
        val filePrefix = directory.absolutePath + File.separator
        try {
            writePlot(pngGenerationContext, filePrefix, array, slice)
        }finally {
            bufferedImageList.push(pngGenerationContext)
        }
    }

    fun writePlot(pngGenerationContext: PNGGenerationContext, filePrefix: String, array: Array<ScaledCandle>, slice: List<ScaledCandle>) {
        val localDateTime = slice.last().source.localDateTime
        val graphics = pngGenerationContext.bufferedImage.graphics as Graphics2D

        writeStoryLines(graphics, array, slice)
        // writeRealCandles(graphics, slice)
        writeStochasticCandles(graphics, slice)

        ImageIO.write(
            pngGenerationContext.bufferedImage,
            "png",
            File("${filePrefix}${pngGenerationContext.dateFormatter.format(localDateTime)}.png")
        )

        graphics.color = Color.WHITE
        graphics.fillRect(0, 0, width, height)

    }

    private fun writeStochasticCandles(
        graphics: Graphics2D,
        slice: List<ScaledCandle>
    ) {
        val candleWidth = 5
        val horizGap = 3 //pixels
        var horizOffset = 3

        slice.forEach { candle ->
            graphics.color = ColorPicker.forNormalCandle(candle)

            graphics.drawLine(
                horizOffset + 2,
                height - (candle.high*100).toInt(),
                horizOffset + 2,
                height - (candle.low*100).toInt(),
            )

            graphics.fillRect(
                horizOffset,
                height - (candle.open.coerceAtMost(candle.close) * height).toInt(),
                candleWidth,
                (abs(candle.open - candle.close) *height).toInt()
            )

            horizOffset += candleWidth + horizGap
        }

    }

    private fun writeRealCandles(
        g2: Graphics2D,
        slice: List<ScaledCandle>
    ) {

        // Calculate the maximum and minimum values for the y-axis
        val maxY: Double = slice.maxOfOrNull { candle -> candle.high }?.toDouble() ?: 0.0 // getMaxValue(high)
        val minY: Double = slice.maxOfOrNull { candle -> candle.low }?.toDouble() ?: 0.0// getMinValue(low)

        // Calculate the scaling factors for the x-axis and y-axis

        // Calculate the scaling factors for the x-axis and y-axis
        val xScale: Double = width.toDouble() / (slice.size + 2)
        val yScale = (height.toDouble() / (maxY - minY))

        // Draw the candlesticks

        // Draw the candlesticks
        for (i in slice.indices) {
            val x = (i + 1) * xScale
            val yOpen: Double = (maxY - slice[i].source.open) * yScale
            val yClose: Double = (maxY - slice[i].source.close) * yScale
            val yHigh: Double = (maxY - slice[i].source.high) * yScale
            val yLow: Double = (maxY - slice[i].source.low) * yScale


            g2.color = ColorPicker.forNormalCandle(slice[i])
            // Draw the candlestick body
            g2.fill(Rectangle2D.Double(x - xScale / 4, yClose, xScale / 2, abs(yOpen - yClose)))

            // Draw the candlestick wick
            g2.draw(Line2D.Double(x, yHigh, x, yLow))

        }
    }

    private fun writeStoryLines(graphics: Graphics2D,  array: Array<ScaledCandle>, slice: List<ScaledCandle>) {
        if(slice[0].direction == Direction.IDLE) {
            return
        }

        val startInclusive = Math.max(0, slice[0].index - LOOKBACK_PERIOD)
        val endExclusive = slice[0].index
        val vertGap = 5
        var vertOffset = 5

        IntStream.range(startInclusive, endExclusive).forEach{ index ->
            val lineColor = ColorPicker.forNormalCandle(array[index])
            slice.all { bar -> bar.source.localDateTime.compareTo(array[index].source.localDateTime) != 0 } ?: throw RuntimeException()

            graphics.color = lineColor
            graphics.drawLine(
                0,
                vertOffset,
                width,
                vertOffset,
            )

            vertOffset += vertGap
        }

    }
}