package com.kftx.threevolumecandles

import com.kftx.threevolumecandles.process.ThreeVolumeCandleProcess
import java.io.File

class Application {
    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val inputFileName = args[0]
            val outFileName = args[1]
            ThreeVolumeCandleProcess.process( File(inputFileName),  File(outFileName))

        }
    }
}