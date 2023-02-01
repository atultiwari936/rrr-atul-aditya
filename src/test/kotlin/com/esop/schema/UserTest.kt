package com.esop.schema

import com.esop.WalletLimitExceededException
import com.esop.constant.MAX_WALLET_CAPACITY
import com.esop.dto.AddWalletDTO
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UserTest {
    val user = User(
        "FirstName",
        "LastName",
        "PhoneNumber",
        "email@email.com",
        "user_username"
    )

    @Test
    fun `it should add to wallet given valid walletData`() {
        val walletData = AddWalletDTO(1000L)

        val responseMessage = user.addToWallet(walletData)

        assertEquals("${walletData.price} amount added to account.", responseMessage)
        assertEquals(1000L, user.userWallet.getFreeMoney())
    }

    @Test
    fun `given more than max wallet amount, add to wallet throws error`() {
        val walletData = AddWalletDTO(MAX_WALLET_CAPACITY + 1)

        val exception = Assertions.assertThrows(WalletLimitExceededException::class.java)
        {
            user.addToWallet(walletData)
        }

        assertEquals("Wallet Limit exceeded", exception.message)
        assertEquals(0, user.userWallet.getFreeMoney())
    }
}