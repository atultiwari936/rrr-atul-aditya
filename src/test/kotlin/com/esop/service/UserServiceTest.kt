package com.esop.service

import com.esop.InventoryLimitExceededException
import com.esop.constant.MAX_INVENTORY_CAPACITY
import com.esop.dto.AddInventoryDTO
import com.esop.dto.AddWalletDTO
import com.esop.dto.UserCreationDTO
import com.esop.schema.Order
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@MicronautTest
class UserServiceTest {

    @Inject
    private lateinit var userService: UserService

    @Test
    fun `should register a valid user`() {
        val user = UserCreationDTO("Sankar", "M", "+917550276216", "sankar@sahaj.ai", "sankar06")
        val expected = mapOf(
            "firstName" to "Sankar",
            "lastName" to "M",
            "phoneNumber" to "+917550276216",
            "email" to "sankar@sahaj.ai",
            "username" to "sankar06"
        )

        //Action
        val response = userService.registerUser(user)

        //Assert
        assertEquals(response, expected)
    }

    @Test
    fun `should check user doesn't exist before placing Order`() {
        val order = Order(
            quantity = 10, type = "BUY", price = 10, userName = "Sankar"
        )
        val expectedErrors = listOf("User doesn't exist.")

        val errors = UserService.orderCheckBeforePlace(order)

        assertEquals(expectedErrors, errors, "user non existent error should be present in the errors list")
    }

    @Test
    fun `should add money to wallet`() {
        val user = UserCreationDTO("Sankar", "M", "+917550276216", "sankar@sahaj.ai", "Sankar")
        userService.registerUser(user)
        val walletDetails = AddWalletDTO(price = 1000)
        val expectedFreeMoney: Long = 1000
        val expectedUsername = "Sankar"

        userService.addingMoney(walletDetails, "Sankar")

        val actualFreeMoney = UserService.userList[expectedUsername]!!.userWallet.getFreeMoney()
        assertEquals(expectedFreeMoney, actualFreeMoney)
    }


    @Test
    fun `should check if return empty list if there is sufficient free amount is in wallet to place BUY order`() {
        val user = UserCreationDTO("Sankar", "M", "+917550276216", "sankar@sahaj.ai", "sankar06")
        userService.registerUser(user)
        userService.addingMoney(AddWalletDTO(price = 100L), userName = "sankar06")
        val order = Order(
            quantity = 10, type = "BUY", price = 10, userName = "sankar06"
        )

        val actualErrors = UserService.orderCheckBeforePlace(order)

        val expectedErrors = emptyList<String>()
        assertEquals(expectedErrors, actualErrors, "error list returned must be empty")
    }

    @Test
    fun `it should return error list with error if there is insufficient free amount in wallet to place BUY order`() {
        val user = UserCreationDTO("Sankar", "M", "+917550276216", "sankar@sahaj.ai", "sankar06")
        userService.registerUser(user)
        val order = Order(
            quantity = 10, type = "BUY", price = 10, userName = "sankar06"
        )
        userService.addingMoney(AddWalletDTO(price = 99L), userName = "sankar06")

        val actualErrors = UserService.orderCheckBeforePlace(order)

        val expectedErrors = listOf("Insufficient funds")
        assertEquals(expectedErrors, actualErrors)
    }

    @Test
    fun `it should return error when the buyer inventory overflows`() {
        val user = UserCreationDTO("Sankar", "M", "+917550276216", "sankar@sahaj.ai", "sankar06")
        userService.registerUser(user)
        val order = Order(
            quantity = 10, type = "BUY", price = 10, userName = "sankar06"
        )
        userService.addingInventory(AddInventoryDTO(MAX_INVENTORY_CAPACITY, "NON_PERFORMANCE"), userName = "sankar06")

        assertThrows<InventoryLimitExceededException> {
            UserService.orderCheckBeforePlace(order)
        }
    }
}