package com.kftx.threevolumecandles.feature

import java.awt.image.BufferedImage
import java.text.SimpleDateFormat

data class PNGGenerationContext(val bufferedImage: BufferedImage, val dateFormatter: SimpleDateFormat)