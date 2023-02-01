package com.esop.schema

import com.esop.dto.AddInventoryDTO
import com.esop.dto.AddWalletDTO
import com.esop.dto.CreateOrderDTO
import com.esop.service.OrderService

class User(
    var firstName: String,
    var lastName: String,
    var phoneNumber: String,
    var email: String,
    var username: String
) {
    val userWallet: Wallet = Wallet()
    val userNonPerfInventory: Inventory = Inventory(type = "NON_PERFORMANCE")
    val userPerformanceInventory: Inventory = Inventory(type = "PERFORMANCE")
    val orderList: ArrayList<Order> = ArrayList()

    fun addToWallet(walletData: AddWalletDTO): String {
        userWallet.addMoneyToWallet(walletData.price!!)
        return "${walletData.price} amount added to account."
    }

    fun addToInventory(inventoryData: AddInventoryDTO): String {
        if (inventoryData.esopType.toString().uppercase() == "NON_PERFORMANCE") {
            userNonPerfInventory.addESOPsToInventory(inventoryData.quantity!!)
            return "${inventoryData.quantity} Non-Performance ESOPs added to account."
        } else if (inventoryData.esopType.toString().uppercase() == "PERFORMANCE") {
            userPerformanceInventory.addESOPsToInventory(inventoryData.quantity!!)
            return "${inventoryData.quantity} Performance ESOPs added to account."
        }
        return "None"
    }

    private fun createBuyOrder(orderDetails: CreateOrderDTO): Order {
        userNonPerfInventory.checkInventoryWillNotOverflowOnAdding(orderDetails.quantity!!)
        userWallet.moveMoneyFromFreeToLockedState(orderDetails.price!! * orderDetails.quantity!!)
        return Order(
            orderID = OrderService.generateOrderId(),
            quantity = orderDetails.quantity!!,
            type = orderDetails.type!!,
            price = orderDetails.price!!,
            userName = this.username
        )
    }

    private fun createSellOrder(orderDetails: CreateOrderDTO): Order {
        userWallet.checkWalletWillNotOverflowOnAdding(orderDetails.price!! * orderDetails.quantity!!)
        if (orderDetails.esopType == "PERFORMANCE") {
            userPerformanceInventory.moveESOPsFromFreeToLockedState(orderDetails.quantity!!)
        } else if (orderDetails.esopType == "NON_PERFORMANCE") {
            userNonPerfInventory.moveESOPsFromFreeToLockedState(orderDetails.quantity!!)
        }
        return Order(
            orderID = OrderService.generateOrderId(),
            quantity = orderDetails.quantity!!,
            type = orderDetails.type!!,
            price = orderDetails.price!!,
            userName = this.username,
            esopType = orderDetails.esopType!!
        )
    }

    fun createOrder(orderDetails: CreateOrderDTO): Order? {
        val order: Order
        if (orderDetails.type == "BUY") {
            order = createBuyOrder(orderDetails)
            orderList.add(order)
            return order
        } else if (orderDetails.type == "SELL") {
            order = createSellOrder(orderDetails)
            orderList.add(order)
            return order
        }
        return null
    }
}