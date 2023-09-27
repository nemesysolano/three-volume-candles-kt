package com.kftx.threevolumecandles.loaders

import com.kftx.threevolumecandles.model.ForexInput
import com.kftx.threevolumecandles.model.ThreeVolumeCandles
import java.io.File
import java.text.SimpleDateFormat
import java.util.StringTokenizer


object CSV {
    val dateTimeFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    fun forexInputStream(file: File): Array<ThreeVolumeCandles> =

        file.readLines().let {self ->
            self.takeLast(self.size-1).map { line ->
                val tokenizer = StringTokenizer(line, ",")
                ThreeVolumeCandles(
                    ForexInput(
                        dateTimeFormatter.parse(tokenizer.nextToken()),
                        tokenizer.nextToken().toDouble(),
                        tokenizer.nextToken().toDouble(),
                        tokenizer.nextToken().toDouble(),
                        tokenizer.nextToken().toDouble(),
                        tokenizer.nextToken().toDouble(),
                        tokenizer.nextToken().toDouble(),
                        tokenizer.nextToken().toDouble(),
                    )
                )
            }
        }.toTypedArray()

}