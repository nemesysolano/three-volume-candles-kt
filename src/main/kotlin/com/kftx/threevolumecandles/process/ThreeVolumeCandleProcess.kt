package com.kftx.threevolumecandles.process

import com.kftx.threevolumecandles.feature.Extractors
import com.kftx.threevolumecandles.loaders.CSV
import com.kftx.threevolumecandles.model.ThreeVolumeCandles
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.io.PrintWriter
import java.util.concurrent.CompletableFuture

object ThreeVolumeCandleProcess {
    fun processAsync(input: File, output: File) {
        val array = CSV.forexInputStream(input)
        FileOutputStream(output).use { outputStream ->
            processAsync(array)
            write(array, outputStream)
        }
    }

    fun process(input: File, output: File) {
        val array = CSV.forexInputStream(input)
        FileOutputStream(output).use { outputStream ->
            process(array)
            write(array, outputStream)
        }
    }

    private fun processAsync(array: Array<ThreeVolumeCandles>) {
        CompletableFuture.allOf(
            CompletableFuture.runAsync { Extractors.normalizeOpen(array) },
            CompletableFuture.runAsync { Extractors.normalizeHigh(array) },
            CompletableFuture.runAsync { Extractors.normalizeLow(array) },
            CompletableFuture.runAsync { Extractors.normalizeClose(array) },
            CompletableFuture.runAsync { Extractors.normalizeThickVolume(array) },
            CompletableFuture.runAsync { Extractors.detectReversion(array) }
        )
    }

    fun process(array: Array<ThreeVolumeCandles>) {
        Extractors.normalizeOpen(array)
        Extractors.normalizeHigh(array)
        Extractors.normalizeLow(array)
        Extractors.normalizeClose(array)
        Extractors.normalizeThickVolume(array)
        Extractors.detectReversion(array)
    }

    /*
    datetime,open,high,low,close,tick_volume,
    * */
    private fun write(array: Array<ThreeVolumeCandles>, output: OutputStream) {
        PrintWriter(output).use { printer ->
            printer.println("datetime, open, high, low, close, normalized_open, normalized_low, normalized_high, normalized_close, normalized_tick_volume, direction")
            array.forEach { row ->
                val forexInput = row.forexInput
                printer.println(String.format("%s,%8.6f,%8.6f,%8.6f,%8.6f,%8.6f,%8.6f,%8.6f,%8.6f,%8.6f, %d",
                    CSV.dateTimeFormatter.format(forexInput.localDateTime),
                    forexInput.open,
                    forexInput.high,
                    forexInput.low,
                    forexInput.close,
                    row.normalizedOpen,
                    row.normalizedHigh,
                    row.normalizedLow,
                    row.normalizedClose,
                    row.normalizedTickVolume,
                    row.reversion
                ))
            }
            printer.flush()
        }
    }
}
