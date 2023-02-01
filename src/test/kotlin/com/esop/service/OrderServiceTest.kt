package com.esop.service

import com.esop.schema.Order
import com.esop.schema.User
import com.esop.service.OrderService.Companion.buyOrders
import com.esop.service.OrderService.Companion.executeOrderMatching
import com.esop.service.OrderService.Companion.sellOrders
import com.esop.service.UserService.Companion.userList
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
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
        val buyOrder = Order(1,10, "BUY", 10, "sankar", null)

        //Act
        executeOrderMatching(buyOrder)

        //Assert
        assertTrue(buyOrders.contains(buyOrder))
    }

    @Test
    fun `It should place SELL order`() {
        //Arrange
        val sellOrder = Order(1,10, "SELL", 10, "kajal", null)

        //Act
        executeOrderMatching(sellOrder)

        //Assert
        assertTrue(sellOrders.contains(sellOrder))
    }

    @Test
    fun `It should match BUY order for existing SELL order`() {
        //Arrange
        userList["kajal"]!!.userNonPerfInventory.addESOPsToInventory(50)
        val sellOrder = Order(1,10, "SELL", 10, "kajal", null)
        userList["kajal"]!!.userNonPerfInventory.moveESOPsFromFreeToLockedState(10)
        executeOrderMatching(sellOrder)

        userList["sankar"]!!.userWallet.addMoneyToWallet(100)
        val buyOrder = Order(2,10, "BUY", 10, "sankar", null)
        userList["sankar"]!!.userWallet.moveMoneyFromFreeToLockedState(100)

        //Act
        executeOrderMatching(buyOrder)

        //Assert
        assertEquals(40, userList["kajal"]!!.userNonPerfInventory.getFreeInventory())
        assertEquals(10, userList["sankar"]!!.userNonPerfInventory.getFreeInventory())
        assertEquals(98, userList["kajal"]!!.userWallet.getFreeMoney())
        assertEquals(0, userList["sankar"]!!.userWallet.getFreeMoney())
    }

    @Test
    fun `It should place 2 SELL orders followed by a BUY order where the BUY order is partial`() {
        //Arrange
        userList["kajal"]!!.userNonPerfInventory.addESOPsToInventory(50)
        val sellOrderByKajal = Order(1,10, "SELL", 10, "kajal", null)
        userList["kajal"]!!.userNonPerfInventory.moveESOPsFromFreeToLockedState(10)
        executeOrderMatching(sellOrderByKajal)

        userList["arun"]!!.userNonPerfInventory.addESOPsToInventory(50)
        val sellOrderByArun = Order(2,10, "SELL", 10, "arun", null)
        userList["arun"]!!.userNonPerfInventory.moveESOPsFromFreeToLockedState(10)
        executeOrderMatching(sellOrderByArun)

        userList["sankar"]!!.userWallet.addMoneyToWallet(250)
        val buyOrderBySankar = Order(3,25, "BUY", 10, "sankar", null)
        userList["sankar"]!!.userWallet.moveMoneyFromFreeToLockedState(250)

        //Act
        executeOrderMatching(buyOrderBySankar)

        //Assert
        assertEquals(40, userList["kajal"]!!.userNonPerfInventory.getFreeInventory())
        assertEquals(40, userList["arun"]!!.userNonPerfInventory.getFreeInventory())
        assertEquals(20, userList["sankar"]!!.userNonPerfInventory.getFreeInventory())
        assertEquals(98, userList["kajal"]!!.userWallet.getFreeMoney())
        assertEquals(98, userList["arun"]!!.userWallet.getFreeMoney())
        assertEquals(50, userList["sankar"]!!.userWallet.getLockedMoney())
        assertEquals("PARTIAL", buyOrders[buyOrders.indexOf(buyOrderBySankar)].orderStatus)
        assertEquals(
            "COMPLETED",
            userList["kajal"]!!.orderList[userList["kajal"]!!.orderList.indexOf(sellOrderByKajal)].orderStatus
        )
        assertEquals(
            "COMPLETED",
            userList["arun"]!!.orderList[userList["arun"]!!.orderList.indexOf(sellOrderByArun)].orderStatus
        )
    }

    @Test
    fun `It should place 2 SELL orders followed by a BUY order where the BUY order is complete`() {
        //Arrange
        userList["kajal"]!!.userNonPerfInventory.addESOPsToInventory(50)
        val sellOrderByKajal = Order(1,10, "SELL", 10, "kajal", null)
        userList["kajal"]!!.userNonPerfInventory.moveESOPsFromFreeToLockedState(10)
        executeOrderMatching(sellOrderByKajal)

        userList["arun"]!!.userNonPerfInventory.addESOPsToInventory(50)
        val sellOrderByArun = Order(2,10, "SELL", 10, "arun", null)
        userList["arun"]!!.userNonPerfInventory.moveESOPsFromFreeToLockedState(10)
        executeOrderMatching(sellOrderByArun)

        userList["sankar"]!!.userWallet.addMoneyToWallet(250)
        val buyOrderBySankar = Order(3,20, "BUY", 10, "sankar", null)
        userList["sankar"]!!.userWallet.moveMoneyFromFreeToLockedState(200)

        //Act
        executeOrderMatching(buyOrderBySankar)

        //Assert
        assertEquals(40, userList["kajal"]!!.userNonPerfInventory.getFreeInventory())
        assertEquals(40, userList["arun"]!!.userNonPerfInventory.getFreeInventory())
        assertEquals(20, userList["sankar"]!!.userNonPerfInventory.getFreeInventory())
        assertEquals(98, userList["kajal"]!!.userWallet.getFreeMoney())
        assertEquals(98, userList["arun"]!!.userWallet.getFreeMoney())
        assertEquals(0, userList["sankar"]!!.userWallet.getLockedMoney())
        assertEquals(
            "COMPLETED",
            userList["sankar"]!!.orderList[userList["sankar"]!!.orderList.indexOf(buyOrderBySankar)].orderStatus
        )
        assertEquals(
            "COMPLETED",
            userList["kajal"]!!.orderList[userList["kajal"]!!.orderList.indexOf(sellOrderByKajal)].orderStatus
        )
        assertEquals(
            "COMPLETED",
            userList["arun"]!!.orderList[userList["arun"]!!.orderList.indexOf(sellOrderByArun)].orderStatus
        )
    }

    @Test
    fun `It should place 1 SELL orders followed by a BUY order where the BUY order is complete`() {
        //Arrange
        userList["kajal"]!!.userNonPerfInventory.addESOPsToInventory(50)
        val sellOrderByKajal = Order(1,10, "SELL", 10, "kajal", null)
        userList["kajal"]!!.userNonPerfInventory.moveESOPsFromFreeToLockedState(10)
        executeOrderMatching(sellOrderByKajal)


        userList["sankar"]!!.userWallet.addMoneyToWallet(250)
        val buyOrderBySankar = Order(2,5, "BUY", 10, "sankar", null)
        userList["sankar"]!!.userWallet.moveMoneyFromFreeToLockedState(50)

        //Act
        executeOrderMatching(buyOrderBySankar)

        //Assert
        assertEquals(40, userList["kajal"]!!.userNonPerfInventory.getFreeInventory())
        assertEquals(5, userList["sankar"]!!.userNonPerfInventory.getFreeInventory())
        assertEquals(49, userList["kajal"]!!.userWallet.getFreeMoney())
        assertEquals(0, userList["sankar"]!!.userWallet.getLockedMoney())
        assertEquals(
            "COMPLETED",
            userList["sankar"]!!.orderList[userList["sankar"]!!.orderList.indexOf(buyOrderBySankar)].orderStatus
        )
        assertEquals(
            "PARTIAL",
            userList["kajal"]!!.orderList[userList["kajal"]!!.orderList.indexOf(sellOrderByKajal)].orderStatus
        )
    }

    @Test
    fun `It should place 1 SELL orders followed by a BUY order where the BUY order is partial`() {
        //Arrange
        userList["kajal"]!!.userNonPerfInventory.addESOPsToInventory(50)
        val sellOrderByKajal = Order(1,10, "SELL", 10, "kajal", null)
        userList["kajal"]!!.userNonPerfInventory.moveESOPsFromFreeToLockedState(10)
        executeOrderMatching(sellOrderByKajal)


        userList["sankar"]!!.userWallet.addMoneyToWallet(250)
        val buyOrderBySankar = Order(2, 15, "BUY", 10, "sankar", null)
        userList["sankar"]!!.userWallet.moveMoneyFromFreeToLockedState(150)

        //Act
        executeOrderMatching(buyOrderBySankar)

        //Assert
        assertEquals(40, userList["kajal"]!!.userNonPerfInventory.getFreeInventory())
        assertEquals(10, userList["sankar"]!!.userNonPerfInventory.getFreeInventory())
        assertEquals(98, userList["kajal"]!!.userWallet.getFreeMoney())
        assertEquals(50, userList["sankar"]!!.userWallet.getLockedMoney())
        assertEquals(
            "PARTIAL",
            userList["sankar"]!!.orderList[userList["sankar"]!!.orderList.indexOf(buyOrderBySankar)].orderStatus
        )
        assertEquals(
            "COMPLETED",
            userList["kajal"]!!.orderList[userList["kajal"]!!.orderList.indexOf(sellOrderByKajal)].orderStatus
        )
    }

    @Test
    fun `It should place 2 BUY orders followed by a SELL order where the SELL order is partial`() {
        //Arrange
        userList["sankar"]!!.userWallet.addMoneyToWallet(100)
        val buyOrderBySankar = Order(1,10, "BUY", 10, "sankar", null)
        userList["sankar"]!!.userWallet.moveMoneyFromFreeToLockedState(100)
        executeOrderMatching(buyOrderBySankar)

        userList["aditya"]!!.userWallet.addMoneyToWallet(100)
        val buyOrderByAditya = Order(2,10, "BUY", 10, "aditya", null)
        userList["sankar"]!!.userWallet.moveMoneyFromFreeToLockedState(100)
        executeOrderMatching(buyOrderByAditya)

        userList["kajal"]!!.userNonPerfInventory.addESOPsToInventory(50)
        val sellOrderByKajal = Order(3,25, "SELL", 10, "kajal", null)
        userList["kajal"]!!.userNonPerfInventory.moveESOPsFromFreeToLockedState(25)

        //Act
        executeOrderMatching(sellOrderByKajal)

        //Assert
        assertEquals(25, userList["kajal"]!!.userNonPerfInventory.getFreeInventory())
        assertEquals(10, userList["sankar"]!!.userNonPerfInventory.getFreeInventory())
        assertEquals(10, userList["aditya"]!!.userNonPerfInventory.getFreeInventory())
        assertEquals(196, userList["kajal"]!!.userWallet.getFreeMoney())
        assertEquals(0, userList["sankar"]!!.userWallet.getFreeMoney())
        assertEquals(0, userList["sankar"]!!.userWallet.getFreeMoney())
        assertEquals("PARTIAL", sellOrders[sellOrders.indexOf(sellOrderByKajal)].orderStatus)
        assertEquals(
            "COMPLETED",
            userList["sankar"]!!.orderList[userList["sankar"]!!.orderList.indexOf(buyOrderBySankar)].orderStatus
        )
        assertEquals(
            "COMPLETED",
            userList["aditya"]!!.orderList[userList["aditya"]!!.orderList.indexOf(buyOrderByAditya)].orderStatus
        )
    }

    @Test
    fun `It should place 2 BUY orders followed by a SELL order where the SELL order is complete`() {
        //Arrange
        userList["kajal"]!!.userWallet.addMoneyToWallet(100)
        val buyOrderByKajal = Order(1,10, "BUY", 10, "kajal", null)
        userList["kajal"]!!.userWallet.moveMoneyFromFreeToLockedState(10 * 10)
        executeOrderMatching(buyOrderByKajal)

        userList["arun"]!!.userWallet.addMoneyToWallet(100)
        val buyOrderByArun = Order(2, 10, "BUY", 10, "arun", null)
        userList["arun"]!!.userWallet.moveMoneyFromFreeToLockedState(10 * 10)
        executeOrderMatching(buyOrderByArun)

        userList["sankar"]!!.userNonPerfInventory.addESOPsToInventory(30)
        val sellOrderBySankar = Order(3,20, "SELL", 10, "sankar", null)
        userList["sankar"]!!.userNonPerfInventory.moveESOPsFromFreeToLockedState(20)

        //Act
        executeOrderMatching(sellOrderBySankar)

        //Assert
        assertEquals(10, userList["kajal"]!!.userNonPerfInventory.getFreeInventory())
        assertEquals(0, userList["kajal"]!!.userWallet.getFreeMoney())

        assertEquals(10, userList["arun"]!!.userNonPerfInventory.getFreeInventory())
        assertEquals(0, userList["arun"]!!.userWallet.getFreeMoney())

        assertEquals(10, userList["sankar"]!!.userNonPerfInventory.getFreeInventory())
        assertEquals(98 + 98, userList["sankar"]!!.userWallet.getFreeMoney())

        assertEquals(
            "COMPLETED",
            userList["sankar"]!!.orderList[userList["sankar"]!!.orderList.indexOf(sellOrderBySankar)].orderStatus
        )
        assertEquals(
            "COMPLETED",
            userList["kajal"]!!.orderList[userList["kajal"]!!.orderList.indexOf(buyOrderByKajal)].orderStatus
        )
        assertEquals(
            "COMPLETED",
            userList["arun"]!!.orderList[userList["arun"]!!.orderList.indexOf(buyOrderByArun)].orderStatus
        )
    }


}
