package com.kftx.threevolumecandles.process

import com.kftx.threevolumecandles.LOOKBACK_PERIOD
import com.kftx.threevolumecandles.feature.PNGGenerationContextList
import com.kftx.threevolumecandles.feature.CandleDriver
import com.kftx.threevolumecandles.feature.CandleScaler
import com.kftx.threevolumecandles.io.CSV
import com.kftx.threevolumecandles.io.PNG
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.FileOutputStream
import java.util.*
import java.util.stream.IntStream

object Process {
    fun writeCandles(input: File, output: File) {
        val stream = Arrays.stream(CandleDriver.symmetricReversion(CandleScaler.h2l2Scaler(CSV.candles(input).toList().toTypedArray())))
        FileOutputStream(output).use { outputStream ->
            CSV.writeCandles(stream, outputStream)
        }
    }

    fun writePlots(input: File, directory: File) {
        val array = CandleDriver.symmetricReversion(CandleScaler.h2l2Scaler(CSV.candles(input).toList().toTypedArray()))
        val PNGGenerationContextList = PNGGenerationContextList()
        val startInclusive = LOOKBACK_PERIOD - 1
        val symbolName = input.name.subSequence(0, input.name.lastIndexOf('.'))
        val symbolDirectory = File("${directory.absolutePath}${File.separatorChar}${symbolName}")
        if(symbolDirectory.exists()) {
            FileUtils.cleanDirectory(directory)
        }else {
            symbolDirectory.mkdirs()
        }

        IntStream.range(startInclusive, array.size).parallel().forEach { index -> PNG.writePlot(input, PNGGenerationContextList, symbolDirectory, array, index) }
    }


}
