package com.esop.schema

import java.math.BigInteger

class PlatformFee{

    companion object {
        var platFee:BigInteger = BigInteger("0")

        fun addPlatformFee(fee: Long){

            if(fee < 0)
                return

            platFee += fee.toBigInteger()

        }

        fun getPlatformFee(): BigInteger {
            return platFee
        }
    }


}