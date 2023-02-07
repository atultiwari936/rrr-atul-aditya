package com.esop

import com.esop.schema.PlatformFee
import com.esop.schema.PlatformFee.Companion.deductPlatformFeeFrom
import com.esop.schema.PlatformFee.Companion.getPlatformFee
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigInteger


class PlatformFeeTest {

    @AfterEach
    fun `it should set the Platform fee to zero`() {
        PlatformFee.totalPlatformFee = BigInteger("0")
    }

    @Test
    fun `it should add the platform fee to the total platform fee`() {
        val amountToBeAddedToPlatformFee = 10L

        PlatformFee.addPlatformFee(amountToBeAddedToPlatformFee)

        Assertions.assertEquals(BigInteger("10"), PlatformFee.getPlatformFee())
    }

    @Test
    fun `it should add the value to the total platform fee which is beyond the Long integer limit`() {
        val amountToBeAddedToPlatformFee: Long = 10
        PlatformFee.totalPlatformFee = BigInteger("9223372036854775807")

        PlatformFee.addPlatformFee(amountToBeAddedToPlatformFee)

        Assertions.assertEquals(BigInteger("9223372036854775817"), PlatformFee.getPlatformFee())
    }

    @Test
    fun `it should not add the value which is less than zero`() {
        val amountToBeAddedToPlatformFee: Long = -1

        Assertions.assertThrows(PlatformFeeLessThanZeroException::class.java) {
            PlatformFee.addPlatformFee(amountToBeAddedToPlatformFee)
        }

        Assertions.assertEquals(BigInteger("0"), PlatformFee.getPlatformFee())
    }

    @Test
    fun `it should deduct platform fee from the given amount as ESOP type is NON_PERFORMANCE`() {
        val amount = 1000L

        val actualAmount = deductPlatformFeeFrom(amount, "NON_PERFORMANCE")

        val expectedAmount = 980L
        val expectedPlatformFee = BigInteger("20")
        assertEquals(expectedAmount, actualAmount)
        assertEquals(expectedPlatformFee, getPlatformFee())
    }

    @Test
    fun `it should not deduct platform fee from the given amount as ESOP type is PERFORMANCE`() {
        val amount = 1000L

        val actualAmount = deductPlatformFeeFrom(amount, "PERFORMANCE")

        val expectedAmount = 1000L
        val expectedPlatformFee = BigInteger("0")
        assertEquals(expectedAmount, actualAmount)
        assertEquals(expectedPlatformFee, getPlatformFee())
    }

}