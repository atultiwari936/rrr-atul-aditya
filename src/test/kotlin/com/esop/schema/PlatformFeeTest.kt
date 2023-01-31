package com.esop.schema

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigInteger

class PlatformFeeTest {

    @AfterEach
    fun `It should reset the Platform fee`(){
        PlatformFee.platFee = BigInteger("0")
    }

    @Test
    fun `It should add the platform fee with the given amount`(){
        var amountToBeAddedToPlatformFee:Long = 10L

        PlatformFee.addPlatformFee(amountToBeAddedToPlatformFee)

        Assertions.assertEquals(BigInteger("10"),PlatformFee.getPlatformFee())
    }

    @Test
    fun `It should add the value beyond the Long integer limit`(){
        var amountToBeAddedToPlatformFee:Long = 10
        PlatformFee.platFee = BigInteger("9223372036854775807")

        PlatformFee.addPlatformFee(amountToBeAddedToPlatformFee)

        Assertions.assertEquals(BigInteger("9223372036854775817"),PlatformFee.getPlatformFee())
    }

    @Test
    fun `It should not add the value which is less than zero`(){
        var amountToBeAddedToPlatformFee:Long = -1

        PlatformFee.addPlatformFee(amountToBeAddedToPlatformFee)

        Assertions.assertEquals(BigInteger("0"),PlatformFee.getPlatformFee())
    }

}