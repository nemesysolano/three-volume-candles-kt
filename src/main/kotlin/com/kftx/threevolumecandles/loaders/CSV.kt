package com.kftx.threevolumecandles.loaders


import com.kftx.threevolumecandles.model.Candle
import java.io.File
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


}