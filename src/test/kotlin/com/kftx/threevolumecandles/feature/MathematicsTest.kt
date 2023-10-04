package com.kftx.threevolumecandles.feature

import mu.KotlinLogging
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource


@Execution(ExecutionMode.CONCURRENT)
class MathematicsTest {
    private val logger = KotlinLogging.logger {}
    @ParameterizedTest
    @CsvSource(value=[
        // Common cases
        "1.71, 1.82, 10, 0.71, 0.82",
        "1.8573, 1.8684, 100, 0.573, 0.684",
        "1.85775, 1.85876, 1000, 0.775, 0.876",
        "1.857137, 1.857938, 10000, 0.137, 0.938",
        "1.8555329, 1.8555391, 100000, 0.329, 0.391",
        // Corner cases
        "1.01, 1.82, 10, 0.01, 0.82",
        "1.8573, 1.8504, 1000, 0.73, 0.04",
        "1.85805, 1.85876, 10000, 0.05, 0.76",
        "1.8555329, 1.855503115, 100000, 0.329, 0.03115",
    ])
    fun testMantissaScaleWithMinMax(a: Double, b:Double, power: Int, minMantissa: Double, maxMantissa: Double) {
        val mantissaFormat = "%12.6f"
        val actualPower = Mathematics.mantissaScale(a, b)
        assertEquals(actualPower, power)
        assertEquals(
            String.format(mantissaFormat, minMantissa).trim(),
            String.format(mantissaFormat, Mathematics.mantissa(a,power)).trim()
        )
        assertEquals(
            String.format(mantissaFormat, maxMantissa).trim(),
            String.format(mantissaFormat, Mathematics.mantissa(b,power)).trim()
        )
        val thread = Thread.currentThread().name
        logger.debug(String.format("%s min=%12.7f (%12.6f), max=%12.7f (%12.7f)", thread, a, Mathematics.mantissa(a,power), b,  Mathematics.mantissa(b, power)))
    }
}