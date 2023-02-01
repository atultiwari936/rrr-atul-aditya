package com.esop

import com.esop.constant.MAX_WALLET_CAPACITY
import com.esop.schema.Wallet
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class WalletTest {

    val wallet = Wallet()

    @Test
    fun `it should add money to wallet`() {
        val amount:Long = 1000

        wallet.addMoneyToWallet(amount)

        Assertions.assertEquals(amount,wallet.getFreeMoney())
    }

    @Test
    fun `it should move money from free to locked state`() {
        val amountToBeAdded: Long = 1000
        val wallet = Wallet()
        wallet.addMoneyToWallet(amountToBeAdded)

        wallet.moveMoneyFromFreeToLockedState(500)

        Assertions.assertEquals(500, wallet.getFreeMoney())
        Assertions.assertEquals(500, wallet.getLockedMoney())
    }

    @Test
    fun `it should not add money to wallet when money is above MAX_WALLET_CAPACITY`() {
        val amountToBeAdded: Long = MAX_WALLET_CAPACITY + 1
        val wallet = Wallet()

        val exception = Assertions.assertThrows(WalletLimitExceededException::class.java)
        {
            wallet.addMoneyToWallet(amountToBeAdded)
        }
        Assertions.assertEquals("Wallet Limit exceeded", exception.message)
        Assertions.assertEquals(0, wallet.getFreeMoney())
    }

    @Test
    fun `it should not move money from free to locked state when money is below given amount`() {
        val amountToBeAdded: Long = 1000
        val wallet = Wallet()
        wallet.addMoneyToWallet(amountToBeAdded)

        wallet.moveMoneyFromFreeToLockedState(1500)

        Assertions.assertEquals(1000, wallet.getFreeMoney())
        Assertions.assertEquals(0, wallet.getLockedMoney())
    }


}
