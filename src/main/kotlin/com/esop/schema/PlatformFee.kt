package com.esop.schema

import java.math.BigInteger

class PlatformFee{

    companion object {
        var platFee:BigInteger = BigInteger("0")

        fun addPlatformFee(fee: Long){

            platFee += fee.toBigInteger()

        }

        fun getPlatformFee(): BigInteger {
            return platFee
        }
    }


}