package com.esop.service

import com.esop.dto.AddInventoryDTO
import com.esop.dto.AddWalletDTO
import com.esop.dto.CreateOrderDTO
import com.esop.schema.User
import com.esop.service.OrderService.Companion.buyOrders
import com.esop.service.OrderService.Companion.sellOrders
import com.esop.service.UserService.Companion.userList
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class OrderServiceTest {
    @BeforeEach
    fun `It should create user`() {
        val buyer1 = User("Sankaranarayanan", "M", "7550276216", "sankaranarayananm@sahaj.ai", "sankar")
        val buyer2 = User("Aditya", "Tiwari", "", "aditya@sahaj.ai", "aditya")
        val seller1 = User("Kajal", "Pawar", "", "kajal@sahaj.ai", "kajal")
        val seller2 = User("Arun", "Murugan", "", "arun@sahaj.ai", "arun")

        userList["sankar"] = buyer1
        userList["aditya"] = buyer2
        userList["kajal"] = seller1
        userList["arun"] = seller2
    }

    @AfterEach
    fun `It should clear the in memory data`() {
        buyOrders.clear()
        sellOrders.clear()
        userList.clear()
    }

    @Test
    fun `It should place BUY order`() {
        //Arrange
        val walletData = AddWalletDTO(100)
        val buyOrder = CreateOrderDTO("BUY", 10, 10, null)
        val orderService = OrderService()
        val userService = UserService()
        userService.addingMoney(walletData, "sankar")

        //Act
        orderService.createOrder("sankar", buyOrder)

        //Assert
        assertEquals(1, buyOrders.size)
    }

    @Test
    fun `It should place SELL order`() {
        //Arrange
        val inventoryData = AddInventoryDTO(10, "NON_PERFORMANCE")
        val sellOrder = CreateOrderDTO("SELL", 10, 10, "NON_PERFORMANCE")
        val orderService = OrderService()
        val userService = UserService()
        userService.addingInventory(inventoryData, "kajal")

        //Act
        orderService.createOrder("kajal", sellOrder)

        //Assert
        assertEquals(1, sellOrders.size)
    }

    @Test
    fun `It should match BUY order for existing SELL order`() {
        //Arrange
        val orderService = OrderService()
        userList["kajal"]!!.userNonPerfInventory.addESOPsToInventory(50)
        val sellOrder = CreateOrderDTO("SELL", 10, 10, "NON_PERFORMANCE")
        orderService.createOrder("kajal", sellOrder)

        userList["sankar"]!!.userWallet.addMoneyToWallet(100)
        val buyOrder = CreateOrderDTO("BUY", 10, 10, null)

        //Act
        orderService.createOrder("sankar", buyOrder)

        //Assert
        assertEquals(40, userList["kajal"]!!.userNonPerfInventory.getFreeInventory())
        assertEquals(10, userList["sankar"]!!.userNonPerfInventory.getFreeInventory())
        assertEquals(98, userList["kajal"]!!.userWallet.getFreeMoney())
        assertEquals(0, userList["sankar"]!!.userWallet.getFreeMoney())
    }

    @Test
    fun `It should place 2 SELL orders followed by a BUY order where the BUY order is partial`() {
        //Arrange
        val orderService = OrderService()
        userList["kajal"]!!.userNonPerfInventory.addESOPsToInventory(50)
        val sellOrderByKajal = CreateOrderDTO("SELL", 10, 10, "NON_PERFORMANCE")
        orderService.createOrder("kajal", sellOrderByKajal)

        userList["arun"]!!.userNonPerfInventory.addESOPsToInventory(50)
        val sellOrderByArun = CreateOrderDTO("SELL", 10, 10, "NON_PERFORMANCE")
        orderService.createOrder("arun", sellOrderByArun)

        userList["sankar"]!!.userWallet.addMoneyToWallet(250)
        val buyOrderBySankar = CreateOrderDTO("BUY", 25, 10, null)

        //Act
        orderService.createOrder("sankar", buyOrderBySankar)

        //Assert
        assertEquals(40, userList["kajal"]!!.userNonPerfInventory.getFreeInventory())
        assertEquals(40, userList["arun"]!!.userNonPerfInventory.getFreeInventory())
        assertEquals(20, userList["sankar"]!!.userNonPerfInventory.getFreeInventory())
        assertEquals(98, userList["kajal"]!!.userWallet.getFreeMoney())
        assertEquals(98, userList["arun"]!!.userWallet.getFreeMoney())
        assertEquals(50, userList["sankar"]!!.userWallet.getLockedMoney())
        assertEquals("PARTIAL", userList["sankar"]!!.orderList.get(0).status)
        assertEquals("COMPLETED", userList["kajal"]!!.orderList.get(0).status)
        assertEquals("COMPLETED", userList["arun"]!!.orderList.get(0).status)
    }

    @Test
    fun `It should place 2 SELL orders followed by a BUY order where the BUY order is complete`() {
        //Arrange
        val orderService = OrderService()
        userList["kajal"]!!.userNonPerfInventory.addESOPsToInventory(50)
        val sellOrderByKajal = CreateOrderDTO("SELL", 10, 10, "NON_PERFORMANCE")
        orderService.createOrder("kajal", sellOrderByKajal)

        userList["arun"]!!.userNonPerfInventory.addESOPsToInventory(50)
        val sellOrderByArun = CreateOrderDTO("SELL", 10, 10, "NON_PERFORMANCE")
        orderService.createOrder("arun", sellOrderByArun)

        userList["sankar"]!!.userWallet.addMoneyToWallet(250)
        val buyOrderBySankar = CreateOrderDTO("BUY", 20, 10, null)

        //Act
        orderService.createOrder("sankar", buyOrderBySankar)

        //Assert
        assertEquals(40, userList["kajal"]!!.userNonPerfInventory.getFreeInventory())
        assertEquals(40, userList["arun"]!!.userNonPerfInventory.getFreeInventory())
        assertEquals(20, userList["sankar"]!!.userNonPerfInventory.getFreeInventory())
        assertEquals(98, userList["kajal"]!!.userWallet.getFreeMoney())
        assertEquals(98, userList["arun"]!!.userWallet.getFreeMoney())
        assertEquals(0, userList["sankar"]!!.userWallet.getLockedMoney())
        assertEquals("COMPLETED", userList["sankar"]!!.orderList.get(0).status)
        assertEquals("COMPLETED", userList["kajal"]!!.orderList.get(0).status)
        assertEquals("COMPLETED", userList["arun"]!!.orderList.get(0).status)
    }

    @Test
    fun `It should place 1 SELL orders followed by a BUY order where the BUY order is complete`() {
        //Arrange
        val orderService = OrderService()
        userList["kajal"]!!.userNonPerfInventory.addESOPsToInventory(50)
        val sellOrderByKajal = CreateOrderDTO("SELL", 10, 10, "NON_PERFORMANCE")
        orderService.createOrder("kajal", sellOrderByKajal)


        userList["sankar"]!!.userWallet.addMoneyToWallet(250)
        val buyOrderBySankar = CreateOrderDTO("BUY", 5, 10, null)

        //Act
        orderService.createOrder("sankar", buyOrderBySankar)

        //Assert
        assertEquals(40, userList["kajal"]!!.userNonPerfInventory.getFreeInventory())
        assertEquals(5, userList["sankar"]!!.userNonPerfInventory.getFreeInventory())
        assertEquals(49, userList["kajal"]!!.userWallet.getFreeMoney())
        assertEquals(0, userList["sankar"]!!.userWallet.getLockedMoney())
        assertEquals("COMPLETED", userList["sankar"]!!.orderList.get(0).status)
        assertEquals("PARTIAL", userList["kajal"]!!.orderList.get(0).status)
    }

    @Test
    fun `It should place 1 SELL orders followed by a BUY order where the BUY order is partial`() {
        //Arrange
        val orderService = OrderService()
        userList["kajal"]!!.userNonPerfInventory.addESOPsToInventory(50)
        val sellOrderByKajal = CreateOrderDTO("SELL", 10, 10, "NON_PERFORMANCE")
        orderService.createOrder("kajal", sellOrderByKajal)


        userList["sankar"]!!.userWallet.addMoneyToWallet(250)
        val buyOrderBySankar = CreateOrderDTO("BUY", 15, 10, null)

        //Act
        orderService.createOrder("sankar", buyOrderBySankar)

        //Assert
        assertEquals(40, userList["kajal"]!!.userNonPerfInventory.getFreeInventory())
        assertEquals(10, userList["sankar"]!!.userNonPerfInventory.getFreeInventory())
        assertEquals(98, userList["kajal"]!!.userWallet.getFreeMoney())
        assertEquals(50, userList["sankar"]!!.userWallet.getLockedMoney())
        assertEquals("PARTIAL", userList["sankar"]!!.orderList.get(0).status)
        assertEquals("COMPLETED", userList["kajal"]!!.orderList.get(0).status)
    }

    @Test
    fun `It should place 2 BUY orders followed by a SELL order where the SELL order is partial`() {
        //Arrange
        val orderService = OrderService()
        userList["sankar"]!!.userWallet.addMoneyToWallet(100)
        val buyOrderBySankar = CreateOrderDTO("BUY", 10, 10, null)
        orderService.createOrder("sankar", buyOrderBySankar)

        userList["aditya"]!!.userWallet.addMoneyToWallet(100)
        val buyOrderByAditya = CreateOrderDTO("BUY", 10, 10, null)
        orderService.createOrder("aditya", buyOrderByAditya)

        userList["kajal"]!!.userNonPerfInventory.addESOPsToInventory(50)
        val sellOrderByKajal = CreateOrderDTO("SELL", 25, 10, "NON_PERFORMANCE")

        //Act
        orderService.createOrder("kajal", sellOrderByKajal)

        //Assert
        assertEquals(25, userList["kajal"]!!.userNonPerfInventory.getFreeInventory())
        assertEquals(10, userList["sankar"]!!.userNonPerfInventory.getFreeInventory())
        assertEquals(10, userList["aditya"]!!.userNonPerfInventory.getFreeInventory())
        assertEquals(196, userList["kajal"]!!.userWallet.getFreeMoney())
        assertEquals(0, userList["sankar"]!!.userWallet.getFreeMoney())
        assertEquals(0, userList["sankar"]!!.userWallet.getFreeMoney())
        assertEquals("PARTIAL", userList["kajal"]!!.orderList.get(0).status)
        assertEquals("COMPLETED", userList["sankar"]!!.orderList.get(0).status)
        assertEquals("COMPLETED", userList["aditya"]!!.orderList.get(0).status)
    }

    @Test
    fun `It should place 2 BUY orders followed by a SELL order where the SELL order is complete`() {
        //Arrange
        val orderService = OrderService()
        userList["kajal"]!!.userWallet.addMoneyToWallet(100)
        val buyOrderByKajal = CreateOrderDTO("BUY", 10, 10, null)
        orderService.createOrder("kajal",buyOrderByKajal)

        userList["arun"]!!.userWallet.addMoneyToWallet(100)
        val buyOrderByArun = CreateOrderDTO("BUY", 10, 10, null)
        orderService.createOrder("arun",buyOrderByArun)

        userList["sankar"]!!.userNonPerfInventory.addESOPsToInventory(30)
        val sellOrderBySankar = CreateOrderDTO("SELL", 20, 10, "NON_PERFORMANCE")

        //Act
        orderService.createOrder("sankar",sellOrderBySankar)

        //Assert
        assertEquals(10, userList["kajal"]!!.userNonPerfInventory.getFreeInventory())
        assertEquals(0, userList["kajal"]!!.userWallet.getFreeMoney())

        assertEquals(10, userList["arun"]!!.userNonPerfInventory.getFreeInventory())
        assertEquals(0, userList["arun"]!!.userWallet.getFreeMoney())

        assertEquals(10, userList["sankar"]!!.userNonPerfInventory.getFreeInventory())
        assertEquals(98 + 98, userList["sankar"]!!.userWallet.getFreeMoney())

        assertEquals("COMPLETED", userList["kajal"]!!.orderList.get(0).status)
        assertEquals("COMPLETED", userList["sankar"]!!.orderList.get(0).status)
        assertEquals("COMPLETED", userList["arun"]!!.orderList.get(0).status)
    }


}
