package com.esop.schema

import com.esop.PlatformFeeLessThanZeroException
import java.math.BigInteger
import kotlin.math.round

private const val FEE_PERCENTAGE = 0.02

class PlatformFee {

    companion object {

        var totalPlatformFee: BigInteger = BigInteger("0")

        fun addPlatformFee(fee: Long) {
            if (fee < 0)
                throw PlatformFeeLessThanZeroException()
            totalPlatformFee += fee.toBigInteger()
        }

        fun getPlatformFee(): BigInteger {
            return totalPlatformFee
        }

        private fun calculateFee(tradedAmount: Long) = round(tradedAmount * FEE_PERCENTAGE).toLong()

        fun deductPlatformFeeFrom(tradedAmount: Long, esopType: String): Long {
            if (tradedAmount < 0) throw IllegalArgumentException("Traded Amount cannot be negative")
            if (esopType == "PERFORMANCE") return tradedAmount

            val fee = calculateFee(tradedAmount)
            totalPlatformFee += fee.toBigInteger()
            return tradedAmount - fee
        }
    }
}