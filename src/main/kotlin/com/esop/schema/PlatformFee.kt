package com.esop.schema

class PlatformFee{

    companion object {
        var platFee:String = "0"

        fun addPlatformFee(fee: Long){

            val feeToBeAdd: String = fee.toString()

            var result = ""
            platFee.reversed()
            feeToBeAdd.reversed()

            val len:Int = maxOf(platFee.length,feeToBeAdd.length)
            var carry = 0

            for (i in (0 until len)){
                var sum: Int = carry
                sum += if(i< platFee.length && i<feeToBeAdd.length)
                    (platFee[i]-48).code + (feeToBeAdd[i]-48).code
                else if(i< platFee.length)
                    (platFee[i]-48).code
                else
                    (feeToBeAdd[i]-48).code
                carry = sum/10
                result += (sum%10).toString()
            }
            if(carry>0)
                result += carry.toString()
            result.reversed()
            platFee = result
        }

    }


}