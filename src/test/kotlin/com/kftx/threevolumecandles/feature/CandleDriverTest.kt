package com.kftx.threevolumecandles.feature

import com.kftx.threevolumecandles.model.Candle
import com.kftx.threevolumecandles.model.ScaledCandle
import mu.KotlinLogging
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.*
import java.util.stream.Stream

@Execution(ExecutionMode.CONCURRENT)
class CandleDriverTest {
    private val logger = KotlinLogging.logger {}

    @ParameterizedTest
    @MethodSource("highestHighIndexTestSource")
    fun highestHighIndexTest(scaledCandles: Array<ScaledCandle>, intervals: List<Triple<Int,Int,Int>>) {
        val thread = Thread.currentThread().name

        intervals.forEach { triple ->
            val lower = triple.first
            val upper = triple.second
            val expected = triple.third
            val actual = CandleDriver.highestHighIndex(scaledCandles, lower, upper).second
            assertEquals(expected, actual)

            logger.debug(String.format("thread=%s", thread))
        }
    }

    @ParameterizedTest
    @MethodSource("lowestLowIndexTestSource")
    fun lowestLowIndexTest(scaledCandles: Array<ScaledCandle>, intervals: List<Triple<Int,Int,Int>>) {
        val thread = Thread.currentThread().name

        intervals.forEach { triple ->
            val lower = triple.first
            val upper = triple.second
            val expected = triple.third
            val actual = CandleDriver.lowestLowIndex(scaledCandles, lower, upper).second
            assertEquals(expected, actual)

            logger.debug(String.format("thread=%s", thread))
        }
    }

    @ParameterizedTest
    @MethodSource("highestCloseIndexTestSource")
    fun highestCloseIndexTest(scaledCandles: Array<ScaledCandle>, intervals: List<Triple<Int,Int,Int>>) {
        val thread = Thread.currentThread().name

        intervals.forEach { triple ->
            val lower = triple.first
            val upper = triple.second
            val expected = triple.third
            val actual = CandleDriver.highestCloseIndex(scaledCandles, lower, upper).second

            assertEquals(expected, actual)

            logger.debug(String.format("thread=%s", thread))
        }
    }

    @ParameterizedTest
    @MethodSource("lowestCloseIndexTestSource")
    fun lowestCloseIndexTest(scaledCandles: Array<ScaledCandle>, intervals: List<Triple<Int,Int,Int>>) {
        val thread = Thread.currentThread().name

        intervals.forEach { triple ->
            val lower = triple.first
            val upper = triple.second
            val expected = triple.third
            val actual = CandleDriver.lowestCloseIndex(scaledCandles, lower, upper).second

            assertEquals(expected, actual)

            logger.debug(String.format("thread=%s", thread))
        }
    }

    @ParameterizedTest
    @MethodSource("symmetricReversionWithScaledCandleTestSource")
    fun symmetricReversionWithScaledCandleTest(array: Array<ScaledCandle>, expected: Int) {
        val thread = Thread.currentThread().name
        val scaledCandle = array.last()
        val actual = CandleDriver.symmetricReversion(scaledCandle, array)
        assertEquals(expected, actual.direction)

        logger.debug(String.format("thread=%s", thread))
    }


    @ParameterizedTest
    @MethodSource("symmetricReversionTestSource")
    fun symmetricReversionTest(array: Array<ScaledCandle>) {
        val thread = Thread.currentThread().name
        val directions = CandleDriver.symmetricReversion(array).map { scaledCandle -> scaledCandle.direction }

        logger.debug{directions}
        logger.debug(String.format("thread=%s", thread))
    }

    companion object {

        @JvmStatic
        fun highestHighIndexTestSource(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    arrayOf(
                        ScaledCandle(Candle(0, Date(), 1.0, 2.0, 1.0, 1.0, 0.0, 0.0, 0.0)),
                        ScaledCandle(Candle(1, Date(), 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0)),
                        ScaledCandle(Candle(2, Date(), 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0)),
                        ScaledCandle(Candle(3, Date(), 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0)),
                    ),
                    listOf(
                        Triple(0, 1, 0),
                        Triple(0, 2, 0),
                        Triple(0, 3, 0),
                    )
                ),
                Arguments.of(
                    arrayOf(
                        ScaledCandle(Candle(0, Date(), 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0)),
                        ScaledCandle(Candle(1, Date(), 1.0, 2.0, 1.0, 1.0, 0.0, 0.0, 0.0)),
                        ScaledCandle(Candle(2, Date(), 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0)),
                        ScaledCandle(Candle(3, Date(), 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0)),
                    ),
                    listOf(
                        Triple(0, 1, 1),
                        Triple(0, 2, 1),
                        Triple(0, 3, 1),
                    )
                ),
                Arguments.of(
                    arrayOf(
                        ScaledCandle(Candle(0, Date(), 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0)),
                        ScaledCandle(Candle(1, Date(), 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0)),
                        ScaledCandle(Candle(2, Date(), 1.0, 2.0, 1.0, 1.0, 0.0, 0.0, 0.0)),
                        ScaledCandle(Candle(3, Date(), 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0)),
                    ),
                    listOf(
                        Triple(1, 3, 2),
                        Triple(2, 3, 2),
                    )
                )
            )

        }

        @JvmStatic
        fun lowestLowIndexTestSource(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    arrayOf(
                        ScaledCandle(Candle(0, Date(), 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0)),
                        ScaledCandle(Candle(1, Date(), 1.0, 1.0, 2.0, 1.0, 0.0, 0.0, 0.0)),
                        ScaledCandle(Candle(2, Date(), 1.0, 1.0, 2.0, 1.0, 0.0, 0.0, 0.0)),
                        ScaledCandle(Candle(3, Date(), 1.0, 1.0, 2.0, 1.0, 0.0, 0.0, 0.0)),
                    ),
                    listOf(
                        Triple(0, 1, 0),
                        Triple(0, 2, 0),
                        Triple(0, 3, 0),
                    )
                ),
                Arguments.of(
                    arrayOf(
                        ScaledCandle(Candle(0, Date(), 1.0, 1.0, 2.0, 1.0, 0.0, 0.0, 0.0)),
                        ScaledCandle(Candle(1, Date(), 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0)),
                        ScaledCandle(Candle(2, Date(), 1.0, 1.0, 2.0, 1.0, 0.0, 0.0, 0.0)),
                        ScaledCandle(Candle(3, Date(), 1.0, 1.0, 2.0, 1.0, 0.0, 0.0, 0.0)),
                    ),
                    listOf(
                        Triple(0, 1, 1),
                        Triple(0, 2, 1),
                        Triple(0, 3, 1)
                    )
                ),
                Arguments.of(
                    arrayOf(
                        ScaledCandle(Candle(0, Date(), 1.0, 1.0, 2.0, 1.0, 0.0, 0.0, 0.0)),
                        ScaledCandle(Candle(1, Date(), 1.0, 1.0, 2.0, 1.0, 0.0, 0.0, 0.0)),
                        ScaledCandle(Candle(2, Date(), 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0)),
                        ScaledCandle(Candle(3, Date(), 1.0, 1.0, 2.0, 1.0, 0.0, 0.0, 0.0)),
                    ),
                    listOf(
                        Triple(1, 3, 2),
                        Triple(2, 3, 2),
                    )
                )
            )

        }
        @JvmStatic
        fun highestCloseIndexTestSource(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    arrayOf(
                        ScaledCandle(Candle(0, Date(), 1.0, 1.0, 1.0, 2.0, 0.0, 0.0, 0.0)),
                        ScaledCandle(Candle(1, Date(), 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0)),
                        ScaledCandle(Candle(2, Date(), 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0)),
                        ScaledCandle(Candle(3, Date(), 1.0, 1.0, 1.0, 3.0, 0.0, 0.0, 0.0)),
                        ScaledCandle(Candle(4, Date(), 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0)),
                    ),
                    listOf(
                        Triple(0, 1, 0),
                        Triple(0, 2, 0),
                        Triple(1, 3, 3),
                        Triple(2, 4, 3),
                    )
                ),
            )
        }

        @JvmStatic
        fun lowestCloseIndexTestSource(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    arrayOf(
                        ScaledCandle(Candle(0, Date(), 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0)),
                        ScaledCandle(Candle(1, Date(), 1.0, 1.0, 1.0, 2.0, 0.0, 0.0, 0.0)),
                        ScaledCandle(Candle(2, Date(), 1.0, 1.0, 1.0, 3.0, 0.0, 0.0, 0.0)),
                        ScaledCandle(Candle(3, Date(), 1.0, 1.0, 1.0, 3.0, 0.0, 0.0, 0.0)),
                        ScaledCandle(Candle(4, Date(), 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0)),
                    ),
                    listOf(
                        Triple(0, 1, 0),
                        Triple(1, 2, 1),
                        Triple(1, 4, 4),
                    )
                ),
            )
        }

        @JvmStatic
        fun symmetricReversionWithScaledCandleTestSource(): Stream<Arguments> = Stream.of(
//            Arguments.of(
//                arrayOf(
//                    ScaledCandle(Candle(0, Date(), 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)),
//                    ScaledCandle(Candle(1, Date(), 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)),
//                    ScaledCandle(Candle(2, Date(), 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)),
//                    ScaledCandle(Candle(3, Date(), 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)),
//                    ScaledCandle(Candle(4, Date(), 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)),//
//                    ScaledCandle(Candle(5, Date(), 1.0, 1.0, 2.0, 1.0, 1.0, 1.0, 1.0)),
//                    ScaledCandle(Candle(6, Date(), 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)),
//                    ScaledCandle(Candle(7, Date(), 1.0, 1.0, 2.0, 1.0, 1.0, 1.0, 1.0)),
//                    ScaledCandle(Candle(8, Date(), 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)),
//                    ScaledCandle(Candle(9, Date(), 1.0, 1.0, 2.0, 1.0, 1.0, 1.0, 1.0)),
//                ),
//                1
//            ),
            Arguments.of(
                arrayOf(
                    ScaledCandle(Candle(0, Date(), 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)),
                    ScaledCandle(Candle(1, Date(), 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)),
                    ScaledCandle(Candle(2, Date(), 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)),
                    ScaledCandle(Candle(3, Date(), 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)),
                    ScaledCandle(Candle(4, Date(), 1.0, 1.0, 1.0, 3.0, 1.0, 1.0, 1.0)),//
                    ScaledCandle(Candle(5, Date(), 1.0, 0.5, 1.0, 1.0, 1.0, 1.0, 1.0)),
                    ScaledCandle(Candle(6, Date(), 1.0, 0.5, 1.0, 1.0, 1.0, 1.0, 1.0)),
                    ScaledCandle(Candle(7, Date(), 1.0, 0.5, 1.0, 1.0, 1.0, 1.0, 1.0)),
                    ScaledCandle(Candle(8, Date(), 1.0, 0.5, 1.0, 1.0, 1.0, 1.0, 1.0)),
                    ScaledCandle(Candle(9, Date(), 1.0, 0.5, 1.0, 1.0, 1.0, 1.0, 1.0)),
                ),
                -1
            )
        )

        @JvmStatic
        fun symmetricReversionTestSource(): Stream<Arguments> = Stream.of(
            Arguments.of(
                arrayOf( // leftHH < rightLL
                    ScaledCandle(Candle(0, Date(), 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0)),
                    ScaledCandle(Candle(1, Date(), 1.0, 2.0, 2.0, 1.0, 0.0, 0.0, 0.0)),
                    ScaledCandle(Candle(2, Date(), 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0)),
                    ScaledCandle(Candle(3, Date(), 1.0, 2.0, 2.0, 1.0, 0.0, 0.0, 0.0)),
                    ScaledCandle(Candle(4, Date(), 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0)),//
                    ScaledCandle(Candle(5, Date(), 1.0, 3.0, 3.0, 1.0, 0.0, 0.0, 0.0)),
                    ScaledCandle(Candle(6, Date(), 1.0, 4.0, 4.0, 1.0, 0.0, 0.0, 0.0)),
                    ScaledCandle(Candle(7, Date(), 1.0, 3.0, 3.0, 1.0, 0.0, 0.0, 0.0)),
                    ScaledCandle(Candle(8, Date(), 1.0, 4.0, 4.0, 1.0, 0.0, 0.0, 0.0)),
                    ScaledCandle(Candle(9, Date(), 1.0, 3.0, 3.0, 1.0, 0.0, 0.0, 0.0)),
                    ScaledCandle(Candle(10, Date(), 1.0, 3.0, 3.0, 1.0, 0.0, 0.0, 0.0)),
                    ScaledCandle(Candle(11, Date(), 1.0, 4.0, 4.0, 1.0, 0.0, 0.0, 0.0)),
                    ScaledCandle(Candle(12, Date(), 1.0, 3.0, 3.0, 1.0, 0.0, 0.0, 0.0)),
                    ScaledCandle(Candle(13, Date(), 1.0, 3.0, 4.0, 1.0, 0.0, 0.0, 0.0)),
                    ScaledCandle(Candle(14, Date(), 1.0, 3.0, 3.0, 1.0, 0.0, 0.0, 0.0)),//
                    ScaledCandle(Candle(15, Date(), 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0)),
                    ScaledCandle(Candle(16, Date(), 1.0, 2.0, 2.0, 1.0, 0.0, 0.0, 0.0)),
                    ScaledCandle(Candle(17, Date(), 1.0, 1.0, 2.0, 1.0, 0.0, 0.0, 0.0)),
                    ScaledCandle(Candle(18, Date(), 1.0, 2.0, 2.0, 1.0, 0.0, 0.0, 0.0)),
                    ScaledCandle(Candle(19, Date(), 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0)),
                )
            )
        )
    }
}