package com.kftx.threevolumecandles.io

import com.kftx.threevolumecandles.LOOKBACK_PERIOD
import com.kftx.threevolumecandles.feature.ColorPicker
import com.kftx.threevolumecandles.feature.PNGGenerationContext
import com.kftx.threevolumecandles.feature.PNGGenerationContextList
import com.kftx.threevolumecandles.model.ScaledCandle
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.abs

object PNG {
    fun writePlot(input: File, bufferedImageList: PNGGenerationContextList, directory: File, array: Array<ScaledCandle>, index: Int) {
        val startInclusive = index - LOOKBACK_PERIOD + 1
        val endInclusive = index
        val pngGenerationContext = bufferedImageList.take()
        val slice = array.slice(IntRange(startInclusive, endInclusive))
        val filePrefix = directory.absolutePath + File.separator + input.name.substring(0, input.name.lastIndexOf('.'))
        try {
            writePlot(pngGenerationContext, filePrefix, slice)
        }finally {
            bufferedImageList.push(pngGenerationContext)
        }
    }

    fun writePlot(pngGenerationContext: PNGGenerationContext, filePrefix: String, slice: List<ScaledCandle>) {
        val localDateTime = slice[0].source.localDateTime
        val witdh = 100 // pixels
        val height = 100 // pixels
        val graphics = pngGenerationContext.bufferedImage.graphics as Graphics2D
        val candleWidth = 15
        val horizGap = 3 //pixels
        var horizOffset = 3

        slice.forEach { candle ->
            val color = ColorPicker.forNormalCandle(candle)
            graphics.color = color

            graphics.drawLine(
                horizOffset + 6,
                height - (candle.high*100).toInt(),
                horizOffset + 6,
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

        ImageIO.write(
            pngGenerationContext.bufferedImage,
            "png",
            File("${filePrefix}-${pngGenerationContext.dateFormatter.format(localDateTime)}.png")
        )
        graphics.color = Color.WHITE
        graphics.fillRect(0, 0, witdh, height)
    }
}