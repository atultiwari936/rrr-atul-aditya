package com.esop

import com.esop.service.PlatformFee
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigInteger


class PlatformFeeTest {

    private lateinit var platformFee: PlatformFee

    @BeforeEach
    fun `it should set the Platform fee to zero`() {
        platformFee = PlatformFee()
    }

    @Test
    fun `it should deduct platform fee from the given amount as ESOP type is NON_PERFORMANCE`() {
        val amount = 1000L

        val actualAmount = platformFee.deductPlatformFeeFrom(amount, "NON_PERFORMANCE")

        val expectedAmount = 980L
        val expectedPlatformFee = BigInteger("20")
        assertEquals(expectedAmount, actualAmount)
        assertEquals(expectedPlatformFee, platformFee.getPlatformFee())
    }

    @Test
    fun `it should not deduct platform fee from the given amount as ESOP type is PERFORMANCE`() {
        val amount = 1000L

        val actualAmount = platformFee.deductPlatformFeeFrom(amount, "PERFORMANCE")

        val expectedAmount = 1000L
        val expectedPlatformFee = BigInteger("0")
        assertEquals(expectedAmount, actualAmount)
        assertEquals(expectedPlatformFee, platformFee.getPlatformFee())
    }

    @Test
    fun `it should throw exception if a invalid traded amount is being passed`() {
        val tradedAmount: Long = -100

        Assertions.assertThrows(IllegalArgumentException::class.java) {
            platformFee.deductPlatformFeeFrom(tradedAmount, "NON_PERFORMANCE")
        }
    }
}