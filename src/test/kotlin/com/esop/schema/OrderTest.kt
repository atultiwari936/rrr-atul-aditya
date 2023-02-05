package com.esop.schema

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class OrderTest {
    @Test
    fun `if status is COMPLETED isComplete should return true`() {
        //Arrange
        val order = Order(1, 10, "BUY", 10, "sankar", null)
        order.remainingQuantity = 0
        order.status = "COMPLETED"

        //Act
        val response = order.isComplete()

        //Assert
        assertTrue(response)
    }

    @Test
    fun `if status is PARTITAL isComplete should return true`() {
        //Arrange
        val order = Order(1, 10, "BUY", 10, "sankar", null)
        order.remainingQuantity = 5
        order.status = "PARTIAL"

        //Act
        val response = order.isComplete()

        //Assert
        assertFalse(response)
    }

    @Test
    fun `if status is PENDING isComplete should return true`() {
        //Arrange
        val order = Order(1, 10, "BUY", 10, "sankar", null)

        //Act
        val response = order.isComplete()

        //Assert
        assertFalse(response)
    }

    @Test
    fun `if remainingQuantity is equal to orderExecuted quantity then status should be COMPLETED`() {
        //Arrange
        val order = Order(1, 10, "BUY", 10, "sankar", null)
        val executedOrder = OrderFilledLog(
            quantity = 10,
            amount = 10,
            esopType = "NON_PERFORMANCE",
            sellerUsername = "kajal",
            buyerUsername = "sankar"
        )

        //Act
        order.updateOrderStatus(executedOrder)

        //Assert
        assertTrue(order.status.equals("COMPLETED", ignoreCase = true))
    }

    @Test
    fun `if remainingQuantity is less than orderExecuted quantity then status should be PARTIAL`() {
        //Arrange
        val order = Order(1, 10, "BUY", 10, "sankar", null)
        val executedOrder = OrderFilledLog(
            quantity = 5,
            amount = 10,
            esopType = "NON_PERFORMANCE",
            sellerUsername = "kajal",
            buyerUsername = "sankar"
        )

        //Act
        order.updateOrderStatus(executedOrder)

        //Assert
        assertTrue(order.status.equals("PARTIAL", ignoreCase = true))
    }

    @Test
    fun `it should subtract quantity give from remaining quantity of order`() {
        //Arrange
        val order = Order(1, 10, "BUY", 10, "sankar", null)
        val quantityToBeUpdated : Long = 8

        //Act
        order.subtractFromRemainingQuantity(quantityToBeUpdated)

        //Assert
        assertEquals(2,order.remainingQuantity)
    }

    @Test
    fun `it order type is BUY then isBuyOrder should return True`() {
        //Arrange
        val order = Order(1, 10, "BUY", 10, "sankar", null)

        //Act
        val response = order.isBuyOrder()

        //Assert
        assertTrue(response)
    }
    @Test
    fun `it order type is not BUY then isBuyOrder should return False`() {
        //Arrange
        val order = Order(1, 10, "SELL", 10, "sankar", "NON_PERFORMANCE")

        //Act
        val response = order.isBuyOrder()

        //Assert
        assertFalse(response)
    }

    @Test
    fun `it order type is SELL then isSellOrder should return True`() {
        //Arrange
        val order = Order(1, 10, "SELL", 10, "sankar", "NON_PERFORMANCE")

        //Act
        val response = order.isSellOrder()

        //Assert
        assertTrue(response)
    }

    @Test
    fun `it order type is BUY then isSellOrder should return False`() {
        //Arrange
        val order = Order(1, 10, "BUY", 10, "sankar", null)

        //Act
        val response = order.isSellOrder()

        //Assert
        assertFalse(response)
    }
}