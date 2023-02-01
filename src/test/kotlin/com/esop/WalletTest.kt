package com.esop

import com.esop.constant.MAX_WALLET_CAPACITY
import com.esop.schema.Wallet
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class WalletTest {

    @AfterEach
    fun `It should clear the in memory data`() {

    }


    @Test
    fun `It should add money to wallet`(){

        val amountToBeAdded: Long = 1000
        val wallet = Wallet()

        wallet.addMoneyToWallet(amountToBeAdded)

        Assertions.assertEquals(wallet.getFreeMoney(),1000)
    }

    @Test
    fun `It should move money from free to locked state`(){

        val amountToBeAdded: Long = 1000
        val wallet = Wallet()
        wallet.addMoneyToWallet(amountToBeAdded)

        wallet.moveMoneyFromFreeToLockedState(500)

        Assertions.assertEquals(500,wallet.getFreeMoney())
        Assertions.assertEquals(500,wallet.getLockedMoney())

    }

    @Test
    fun `It should not add money to wallet instead it will throws exception`(){

        val amountToBeAdded: Long = MAX_WALLET_CAPACITY + 1
        val wallet = Wallet()

        Assertions.assertThrows(WalletLimitExceededException::class.java)
        {
            wallet.addMoneyToWallet(amountToBeAdded)
        }
        Assertions.assertEquals(wallet.getFreeMoney(),0)
    }

}
