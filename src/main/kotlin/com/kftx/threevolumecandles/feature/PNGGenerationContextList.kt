package com.kftx.threevolumecandles.feature

import java.util.concurrent.LinkedBlockingDeque
import java.text.SimpleDateFormat
import java.util.stream.IntStream
import javax.imageio.ImageIO

class PNGGenerationContextList: LinkedBlockingDeque<PNGGenerationContext>(Runtime.getRuntime().availableProcessors() -1) {
    init {
       val capacity = Runtime.getRuntime().availableProcessors() -1
       IntStream.range(0, capacity).forEach { this.push(
            PNGGenerationContext(
                ImageIO.read(PNGGenerationContextList::class.java.getResourceAsStream("/CandlePlotTemplate.png")),
                SimpleDateFormat("yyyyMMddHHmmss"))
            )
       }
    }

}