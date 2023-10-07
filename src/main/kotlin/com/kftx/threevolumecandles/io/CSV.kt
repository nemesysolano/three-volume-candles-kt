package com.kftx.threevolumecandles.io


import com.kftx.threevolumecandles.model.Candle
import com.kftx.threevolumecandles.model.ScaledCandle
import java.io.File
import java.io.OutputStream
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.StringTokenizer
import java.util.stream.Stream


object CSV {
    val dateTimeFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    fun candles(file: File): Stream<Candle> {
        var index = 0
        return file.readLines().let { self ->
            self.takeLast(self.size - 1).map { line ->
                val tokenizer = StringTokenizer(line, ",")
                Candle(
                    index++,
                    dateTimeFormatter.parse(tokenizer.nextToken()),
                    tokenizer.nextToken().toDouble(),
                    tokenizer.nextToken().toDouble(),
                    tokenizer.nextToken().toDouble(),
                    tokenizer.nextToken().toDouble(),
                    tokenizer.nextToken().toDouble(),
                    tokenizer.nextToken().toDouble(),
                    tokenizer.nextToken().toDouble(),
                )
            }
        }.stream()

    }

    fun writeCandles(array: Stream<ScaledCandle>, output: OutputStream) {
        PrintWriter(output).use { printer ->
            printer.println("datetime, open, high, low, close, tick_volume, normalized_open, normalized_high, normalized_low, normalized_close, normalized_tick_volume, direction")
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