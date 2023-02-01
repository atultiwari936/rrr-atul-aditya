package com.esop.schema

import com.esop.constant.MAX_WALLET_CAPACITY

class Wallet{
    private var freeMoney: Long = 0
    private var lockedMoney: Long = 0

    private fun totalMoneyInWallet(): Long {
        return freeMoney + lockedMoney
    }

    private fun willWalletOverflowOnAdding(amount: Long): Boolean {
        return amount + totalMoneyInWallet() > MAX_WALLET_CAPACITY
    }

    fun checkWalletWillNotOverflowOnAdding(amount: Long) {
        if (willWalletOverflowOnAdding(amount)) throw WalletLimitExceededException()
    }

    fun addMoneyToWallet(amountToBeAdded : Long){
        checkWalletWillNotOverflowOnAdding(amountToBeAdded)

        this.freeMoney = this.freeMoney + amountToBeAdded
    }

    fun moveMoneyFromFreeToLockedState(amountToBeLocked : Long){
        if( this.freeMoney < amountToBeLocked) throw InsufficientFundException()
        this.freeMoney = this.freeMoney - amountToBeLocked
        this.lockedMoney = this.lockedMoney + amountToBeLocked
    }

    fun getFreeMoney():Long{
        return freeMoney
    }

    fun getLockedMoney():Long{
        return lockedMoney
    }

    fun removeMoneyFromLockedState( amountToBeRemoved: Long){
        this.lockedMoney = this.lockedMoney - amountToBeRemoved
    }
}