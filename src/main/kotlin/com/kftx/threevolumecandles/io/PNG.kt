package com.kftx.threevolumecandles.io

import com.kftx.threevolumecandles.LOOKBACK_PERIOD
import com.kftx.threevolumecandles.feature.ColorPicker
import com.kftx.threevolumecandles.feature.PNGGenerationContext
import com.kftx.threevolumecandles.feature.PNGGenerationContextList
import com.kftx.threevolumecandles.model.Direction
import com.kftx.threevolumecandles.model.ScaledCandle
import java.awt.Color
import java.awt.Graphics2D
import java.io.File
import java.lang.RuntimeException
import java.util.stream.IntStream
import javax.imageio.ImageIO
import kotlin.math.abs

object PNG {
    fun writePlot(input: File, bufferedImageList: PNGGenerationContextList, directory: File, array: Array<ScaledCandle>, index: Int) {
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
        val localDateTime = slice[0].source.localDateTime
        val width = 100 // pixels
        val height = 100 // pixels
        val graphics = pngGenerationContext.bufferedImage.graphics as Graphics2D

        writeStoryLines(graphics, width, array, slice);
        writeStochasticCandles(graphics, height, slice)

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
        height: Int,
        slice: List<ScaledCandle>
    ) {
        val candleWidth = 15
        val horizGap = 3 //pixels
        var horizOffset = 3

        slice.forEach { candle ->
            graphics.color = ColorPicker.forNormalCandle(candle)

            graphics.drawLine(
                horizOffset + 6,
                height - (candle.high*100).toInt(),
                horizOffset + 6,
                ((height - candle.low)*100).toInt(),
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

    private fun writeStoryLines(graphics: Graphics2D, width: Int, array: Array<ScaledCandle>, slice: List<ScaledCandle>) {
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