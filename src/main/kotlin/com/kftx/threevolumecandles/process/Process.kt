package com.kftx.threevolumecandles.process

import com.kftx.threevolumecandles.feature.CandleDriver
import com.kftx.threevolumecandles.feature.CandleScaler
import com.kftx.threevolumecandles.loaders.CSV
import com.kftx.threevolumecandles.model.ScaledCandle
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.*
import java.util.stream.Stream

object Process {

    fun normalCandles(input: File, output: File) {
        val stream = Arrays.stream(CandleDriver.symmetricReversion(CandleScaler.h2l2Scaler(CSV.candles(input).toList().toTypedArray())))
        FileOutputStream(output).use { outputStream ->
            write(stream, outputStream)
        }
    }

    fun normalCandlePlots(input: File, directory: File) {
        val plotDateTimeFormatter = SimpleDateFormat("yyyyMMddHHmmss")
        val array = CSV.candles(input)

//        directory.listFiles().forEach { file -> if(!file.name.equals(".gitkeep")) file.delete() }
//        val plots = CandleScaler.plots(array)
//        val filePrefix = directory.absolutePath + File.separator + input.name.substring(0, input.name.lastIndexOf('.'))
//        for(index in 0..11) {
//            val plot = plots[index]
//            ImageIO.write(
//                plot.image,
//                "png",
//                File("${filePrefix}-${plotDateTimeFormatter.format(plot.localDateTime)}.png")
//            )
//        }

    }

    private fun write(array: Stream<ScaledCandle>, output: OutputStream) {
        PrintWriter(output).use { printer ->
            printer.println("datetime, open, high, low, close, tick_volume, normalized_open, normalized_low, normalized_high, normalized_close, normalized_tick_volume, direction")
            array.forEach { row ->
                val forexInput = row.source

                printer.println(String.format("%s,%12.8f,%12.8f,%12.8f,%12.8f,%12.8f,%12.8f,%12.8f,%12.8f,%12.8f,%12.8f, %d",
                    CSV.dateTimeFormatter.format(forexInput.localDateTime),
                    forexInput.open,
                    forexInput.high,
                    forexInput.low,
                    forexInput.close,
                    forexInput.tickVolume,
                    row.open,
                    row.high,
                    row.low,
                    row.close,
                    row.tickVolume,
                    row.direction
                ))
            }
            printer.flush()
        }
    }
}
