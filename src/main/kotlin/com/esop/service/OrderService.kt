package com.esop.service


import com.esop.constant.errors
import com.esop.dto.CreateOrderDTO
import com.esop.exception.OrderCreationFailed
import com.esop.schema.*
import jakarta.inject.Singleton
import java.util.*
import kotlin.math.round

@Singleton
class OrderService {
    companion object {
        var orderId = 1L;

        var buyOrders = mutableListOf<Order>()
        var sellOrders = mutableListOf<Order>()

        @Synchronized
        fun generateOrderId(): Long {
            return orderId++
        }

        fun getOrderExecutedQuanity(buyOrder: Order, sellOrder: Order): Long {
            val result =
                if (buyOrder.remainingQuantity <= sellOrder.remainingQuantity) buyOrder.remainingQuantity else sellOrder.remainingQuantity
            return result
        }

        fun moveMoneyFromBuyersAccountToSellersAccount(orderFilledLog: OrderFilledLog) {
            val buyer: User = UserService.getUser(orderFilledLog.buyerUsername!!)
            val seller: User = UserService.getUser(orderFilledLog.sellerUsername!!)

            val amountToBeDeductedFromLockedStateOfBuyersAccount = orderFilledLog.getTotalOrderPrice()
            buyer.userWallet.removeMoneyFromLockedState(amountToBeDeductedFromLockedStateOfBuyersAccount)

            var amountToBeAddedToFreeWalletOfSellersAccount = amountToBeDeductedFromLockedStateOfBuyersAccount
            if (orderFilledLog.esopType == "NON_PERFORMANCE") {
                amountToBeAddedToFreeWalletOfSellersAccount -= round(amountToBeDeductedFromLockedStateOfBuyersAccount * 0.02).toLong()
            }
            seller.userWallet.addMoneyToWallet(amountToBeAddedToFreeWalletOfSellersAccount)
        }

        fun moveInventoryFromSellersAccountToBuyersAccount(orderFilledLog: OrderFilledLog) {
            val buyer: User = UserService.getUser(orderFilledLog.buyerUsername!!)
            val seller: User = UserService.getUser(orderFilledLog.sellerUsername!!)

            val inventoryToBeDeductedFromLockedStateOfSellersAccountAndAddedToFreeInventoryOfBuyersAccount =
                orderFilledLog.quantity
            if (orderFilledLog.esopType == "PERFORMANCE") {
                seller.userPerformanceInventory.removeESOPsFromLockedState(
                    inventoryToBeDeductedFromLockedStateOfSellersAccountAndAddedToFreeInventoryOfBuyersAccount
                )
            } else if (orderFilledLog.esopType == "NON_PERFORMANCE") {
                seller.userNonPerfInventory.removeESOPsFromLockedState(
                    inventoryToBeDeductedFromLockedStateOfSellersAccountAndAddedToFreeInventoryOfBuyersAccount
                )
            }
            buyer.userNonPerfInventory.addESOPsToInventory(
                inventoryToBeDeductedFromLockedStateOfSellersAccountAndAddedToFreeInventoryOfBuyersAccount
            )
        }

        fun moveExcessAmountLockedFromLockedStateOfBuyersAccountToFreeWallet(
            buyerUserName: String,
            amountToBeMovedFromLockedStateToFreeWallet: Long
        ) {
            val buyer: User = UserService.getUser(buyerUserName)

            buyer.userWallet.addMoneyToWallet(amountToBeMovedFromLockedStateToFreeWallet)
            buyer.userWallet.removeMoneyFromLockedState(amountToBeMovedFromLockedStateToFreeWallet)
        }

        fun executeBuyOrderMatching(buyOrder: Order) {
            val sellOrderList: List<Order> = sortAscending()
            for (sellOrder in sellOrderList) {
                if (buyOrder.isComplete()) {
                    break
                }
                if (sellOrder.price <= buyOrder.price) {
                    val orderExecutionPrice = sellOrder.price
                    val orderExecutionQuanity = getOrderExecutedQuanity(buyOrder, sellOrder)

                    val orderFilledLog = OrderFilledLog(
                        quantity = orderExecutionQuanity,
                        amount = orderExecutionPrice,
                        esopType = sellOrder.esopType,
                        sellerUsername = sellOrder.userName,
                        buyerUsername = buyOrder.userName
                    )

                    moveMoneyFromBuyersAccountToSellersAccount(orderFilledLog)
                    moveInventoryFromSellersAccountToBuyersAccount(orderFilledLog)

                    val excessAmountLockedInBuyersAccount = (buyOrder.price - sellOrder.price) * orderExecutionQuanity
                    moveExcessAmountLockedFromLockedStateOfBuyersAccountToFreeWallet(
                        orderFilledLog.buyerUsername!!,
                        excessAmountLockedInBuyersAccount
                    )

                    if (orderFilledLog.esopType == "NON_PERFORMANCE") {
                        val platformFeeForOrderExecuted = round(orderFilledLog.getTotalOrderPrice() * 0.02).toLong()
                        PlatformFee.addPlatformFee(platformFeeForOrderExecuted)
                    }

                    sellOrder.updateOrderStatus(orderFilledLog)
                    sellOrder.subtractFromRemainingQuantity(orderExecutionQuanity)

                    buyOrder.updateOrderStatus(orderFilledLog)
                    buyOrder.subtractFromRemainingQuantity(orderExecutionQuanity)

                    if (buyOrder.isComplete()) {
                        buyOrders.remove(buyOrder)
                    }
                    if (sellOrder.isComplete()) {
                        sellOrders.remove(sellOrder)
                    }

                    buyOrder.addOrderFilledLogs(orderFilledLog)
                    sellOrder.addOrderFilledLogs(orderFilledLog)
                }
            }
        }

        fun executeSellOrderMatching(sellOrder: Order) {
            val buyOrderList: List<Order> =
                buyOrders.sortedWith(compareByDescending<Order> { it.price }.thenBy { it.timeStamp })
            for (buyOrder in buyOrderList) {
                if (sellOrder.isComplete()) {
                    break
                }
                if (sellOrder.price <= buyOrder.price) {
                    val orderExecutionPrice = sellOrder.price
                    val orderExecutionQuanity = getOrderExecutedQuanity(buyOrder, sellOrder)

                    val orderFilledLog = OrderFilledLog(
                        quantity = orderExecutionQuanity,
                        amount = orderExecutionPrice,
                        esopType = sellOrder.esopType,
                        sellerUsername = sellOrder.userName,
                        buyerUsername = buyOrder.userName
                    )

                    moveMoneyFromBuyersAccountToSellersAccount(orderFilledLog)
                    moveInventoryFromSellersAccountToBuyersAccount(orderFilledLog)

                    val excessAmountLockedInBuyersAccount = (buyOrder.price - sellOrder.price) * orderExecutionQuanity
                    moveExcessAmountLockedFromLockedStateOfBuyersAccountToFreeWallet(
                        orderFilledLog.buyerUsername!!,
                        excessAmountLockedInBuyersAccount
                    )

                    if (orderFilledLog.esopType == "NON_PERFORMANCE") {
                        val platformFeeForOrderExecuted = round(orderFilledLog.getTotalOrderPrice() * 0.02).toLong()
                        PlatformFee.addPlatformFee(platformFeeForOrderExecuted)
                    }

                    sellOrder.updateOrderStatus(orderFilledLog)
                    sellOrder.subtractFromRemainingQuantity(orderExecutionQuanity)

                    buyOrder.updateOrderStatus(orderFilledLog)
                    buyOrder.subtractFromRemainingQuantity(orderExecutionQuanity)

                    if (buyOrder.isComplete()) {
                        buyOrders.remove(buyOrder)
                    }
                    if (sellOrder.isComplete()) {
                        sellOrders.remove(sellOrder)
                    }

                    buyOrder.addOrderFilledLogs(orderFilledLog)
                    sellOrder.addOrderFilledLogs(orderFilledLog)
                }
            }
        }

        private fun sortAscending(): List<Order> {
            return sellOrders.sortedWith<Order>(object : Comparator<Order> {
                override fun compare(o1: Order, o2: Order): Int {

                    if (o1.inventoryPriority != o2.inventoryPriority)
                        return o1.inventoryPriority - o2.inventoryPriority

                    if (o1.inventoryPriority == 1) {
                        if (o1.timeStamp < o2.timeStamp)
                            return -1
                        return 1
                    }

                    if (o1.price == o2.price) {
                        if (o1.timeStamp < o2.timeStamp)
                            return -1
                        return 1
                    }
                    if (o1.price < o2.price)
                        return -1
                    return 1
                }
            })
        }

        fun executeOrderMatching(order: Order): Map<String, Any> {
            if (order.type == "BUY") {
                executeBuyOrderMatching(order)
            } else {
                executeSellOrderMatching(order)
            }
            return mapOf("orderId" to order.orderID)
        }

        fun orderHistory(userName: String): Any {
            val userErrors = ArrayList<String>()
            if (!UserService.userList.contains(userName)) {
                errors["USER_DOES_NOT_EXISTS"]?.let { userErrors.add(it) }
                return mapOf("error" to userErrors)
            }
            val orderDetails = UserService.userList.get(userName)!!.orderList
            val orderHistory = ArrayList<History>()


            if (orderDetails.size >= 0) {
                for (orders in orderDetails) {
                    orderHistory.add(
                        History(
                            orders.orderID,
                            orders.quantity,
                            orders.type,
                            orders.price,
                            orders.status,
                            orders.executionHistory
                        )
                    )
                }

            }
            return orderHistory
        }

    }

    fun createOrder(userName: String, orderDetails: CreateOrderDTO): CreateOrderResponseDTO {
        UserService.checkIfUserExists(userName)

        val user: User = UserService.getUser(userName)
        val orderResult: Optional<Order> = user.createOrder(orderDetails)

        if (orderResult.isEmpty) {
            throw OrderCreationFailed("Order creation failed.")
        }

        val order = orderResult.get()

        if (order.isBuyOrder()) {
            buyOrders.add(order)
        } else if (order.isSellOrder()) {
            sellOrders.add(order)
        }
        executeOrderMatching(order)

        return CreateOrderResponseDTO(
            orderId = order.orderID,
            quantity = order.quantity,
            price = order.price,
            type = order.type,
            esopType = order.esopType
        )

    }
}

