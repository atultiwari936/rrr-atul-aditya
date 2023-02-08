package com.esop.schema

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class WalletTest {


    @Test
    fun `it should move money from locked to free state`(){
        val wallet = Wallet()
        wallet.addMoneyToWallet(100)
        wallet.moveMoneyFromFreeToLockedState(50)

        wallet.moveMoneyFromLockedToFree(24)

        val expectedFreeAmount = 74L
        val expectedLockedAmount = 26L

        assertEquals(expectedFreeAmount, wallet.getFreeMoney())
        assertEquals(expectedLockedAmount,wallet.getLockedMoney())
    }

    @Test
    fun `it should move money from locked to free state for negative amount given`(){
        val wallet = Wallet()
        wallet.addMoneyToWallet(100)
        wallet.moveMoneyFromFreeToLockedState(50)

        wallet.moveMoneyFromLockedToFree(-25)

        val expectedFreeAmount = 25L
        val expectedLockedAmount = 75L

        assertEquals(expectedFreeAmount, wallet.getFreeMoney())
        assertEquals(expectedLockedAmount,wallet.getLockedMoney())
    }

    @Test
    fun `it should check for insufficient funds`(){
        val wallet = Wallet()
        wallet.addMoneyToWallet(100)

        val response = wallet.moveMoneyFromFreeToLockedState(150)

        val expectedResult = "Insufficient funds"
        assertEquals(expectedResult,response)
    }

}