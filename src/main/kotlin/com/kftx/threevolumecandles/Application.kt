package com.kftx.threevolumecandles

import com.kftx.threevolumecandles.process.Process
import java.io.File


fun main(args: Array<String>) {
    val command = args[0]
    val inputFileName = args[1]
    val outFileName = args[2]
    val outDirectory = args[2]

    when(command) {
        "excel" -> Process.writeCandles( File(inputFileName),  File(outFileName))
        "plots" -> Process.writePlots( File(inputFileName),  File(outDirectory))
        else -> {}
    }


}