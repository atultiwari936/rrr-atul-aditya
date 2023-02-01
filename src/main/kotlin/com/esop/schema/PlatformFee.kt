package com.esop.schema

class PlatformFee{

    companion object {
        var platformFees:String = "0"

        fun addPlatformFee(fee: Long){

            val feeToBeAdd: String = fee.toString()

            var result = ""
            platformFees.reversed()
            feeToBeAdd.reversed()

            val len:Int = maxOf(platformFees.length,feeToBeAdd.length)
            var carry = 0

            for (i in (0 until len)){
                var sum: Int = carry
                sum += if(i< platformFees.length && i<feeToBeAdd.length)
                    (platformFees[i]-48).code + (feeToBeAdd[i]-48).code
                else if(i< platformFees.length)
                    (platformFees[i]-48).code
                else
                    (feeToBeAdd[i]-48).code
                carry = sum/10
                result += (sum%10).toString()
            }
            if(carry>0)
                result += carry.toString()
            result.reversed()
            platformFees = result
        }

    }


}