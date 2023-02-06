package com.esop.service


import com.esop.constant.errors
import com.esop.repository.OrderRecords
import com.esop.repository.UserRecords
import com.esop.schema.History
import com.esop.schema.Order
import com.esop.schema.OrderFilledLog
import com.esop.schema.PlatformFee.Companion.addPlatformFee
import jakarta.inject.Singleton
import kotlin.math.min
import kotlin.math.round

@Singleton
class OrderService(
    private val userRecords: UserRecords,
    private val orderRecords: OrderRecords
) {
    companion object {
        private var orderId = 1L

        var buyOrders = mutableListOf<Order>()
        var sellOrders = mutableListOf<Order>()
    }

    @Synchronized
    fun generateOrderId(): Long {
        return orderId++
    }

    private fun updateOrderDetailsForBuy(
        userName: String,
        prevQuantity: Long,
        remainingQuantity: Long,
        sellerOrder: Order,
        buyerOrder: Order
    ) {
        // Deduct money of quantity taken from buyer
        val amountToBeDeductedFromLockedState = sellerOrder.price * (prevQuantity - remainingQuantity)
        userRecords.getUser(userName)!!.userWallet.removeMoneyFromLockedState(amountToBeDeductedFromLockedState)

        // Add money of quantity taken from seller
        var amountToBeAddedToSellersAccount = amountToBeDeductedFromLockedState
        if (sellerOrder.esopType == "NON_PERFORMANCE") {
            amountToBeAddedToSellersAccount -= round(amountToBeDeductedFromLockedState * 0.02).toLong()
            addPlatformFee(round(amountToBeDeductedFromLockedState * 0.02).toLong())

        }
        userRecords.getUser(sellerOrder.userName)!!.userWallet.addMoneyToWallet(amountToBeAddedToSellersAccount)

        // Deduct inventory of stock from sellers inventory based on its type
        if (sellerOrder.esopType == "PERFORMANCE") {
            userRecords.getUser(sellerOrder.userName)!!.userPerformanceInventory.removeESOPsFromLockedState(
                prevQuantity - remainingQuantity
            )
        }

        if (sellerOrder.esopType == "NON_PERFORMANCE") {
            userRecords.getUser(sellerOrder.userName)!!.userNonPerfInventory.removeESOPsFromLockedState(
                prevQuantity - remainingQuantity
            )
        }

        // Add purchased inventory to buyer
        userRecords.getUser(userName)!!.userNonPerfInventory.addESOPsToInventory(prevQuantity - remainingQuantity)

        // Add buyers money back to free from locked
        userRecords.getUser(userName)!!.userWallet.addMoneyToWallet((buyerOrder.price - sellerOrder.price) * (prevQuantity - remainingQuantity))
        userRecords.getUser(userName)!!.userWallet.removeMoneyFromLockedState((buyerOrder.price - sellerOrder.price) * (prevQuantity - remainingQuantity))
    }

    private fun updateOrderDetailsForSell(
        userName: String,
        prevQuantity: Long,
        remainingQuantity: Long,
        buyerOrder: Order,
        sellerOrder: Order
    ) {

        // Deduct inventory of stock from sellers inventory based on its type
        if (sellerOrder.esopType == "PERFORMANCE") {
            userRecords.getUser(userName)!!.userPerformanceInventory.removeESOPsFromLockedState(prevQuantity - remainingQuantity)
        }

        if (sellerOrder.esopType == "NON_PERFORMANCE") {
            userRecords.getUser(userName)!!.userNonPerfInventory.removeESOPsFromLockedState(prevQuantity - remainingQuantity)
        }

        // Add inventory to buyers stock
        userRecords.getUser(buyerOrder.userName)!!.userNonPerfInventory.addESOPsToInventory(prevQuantity - remainingQuantity)

        // Deduct money from buyers wallet
        userRecords.getUser(buyerOrder.userName)!!.userWallet.removeMoneyFromLockedState((sellerOrder.price * (prevQuantity - remainingQuantity)))

        // Add money to sellers wallet
        var totOrderPrice = sellerOrder.price * (prevQuantity - remainingQuantity)
        if (sellerOrder.esopType == "NON_PERFORMANCE") {
            totOrderPrice -= round(totOrderPrice * 0.02).toLong()
            addPlatformFee(round(totOrderPrice * 0.02).toLong())
        }
        userRecords.getUser(userName)!!.userWallet.addMoneyToWallet(totOrderPrice)

        // Add buyers luck back to free from locked
        userRecords.getUser(buyerOrder.userName)!!.userWallet.addMoneyToWallet((buyerOrder.price - sellerOrder.price) * (prevQuantity - remainingQuantity))
        userRecords.getUser(buyerOrder.userName)!!.userWallet.removeMoneyFromLockedState((buyerOrder.price - sellerOrder.price) * (prevQuantity - remainingQuantity))

    }


    fun placeOrder(order: Order): Map<String, Any> {

        order.orderID = orderRecords.generateOrderId()
        order.remainingQuantity = order.quantity

        if (order.type == "BUY") {

            orderRecords.addTobuyOrders(order)
            val sortedSellOrders = orderRecords.sortSellOrder()

            for (bestSellOrder in sortedSellOrders) {
                if (order.remainingQuantity == 0L) {
                    break
                }
                if ((order.price >= bestSellOrder.price) && (bestSellOrder.remainingQuantity > 0)) {
                    val remainingQuantity = order.remainingQuantity
                    val orderExecutionPrice = bestSellOrder.price
                    val orderExecutionQuantity = min(order.remainingQuantity, bestSellOrder.remainingQuantity)

                    val orderLog = OrderFilledLog(
                        orderExecutionQuantity,
                        orderExecutionPrice,
                        bestSellOrder.esopType,
                        bestSellOrder.userName,
                        order.userName
                    )

                    bestSellOrder.remainingQuantity = bestSellOrder.remainingQuantity - orderExecutionQuantity
                    order.remainingQuantity = order.remainingQuantity - orderExecutionQuantity

                    updateStatus(bestSellOrder)
                    updateStatus(order)

                    bestSellOrder.orderFilledLogs.add(orderLog)
                    order.orderFilledLogs.add(orderLog)

                    updateOrderDetailsForBuy(
                        order.userName,
                        remainingQuantity,
                        order.remainingQuantity,
                        bestSellOrder,
                        order
                    )

                }
            }
        } else {
            orderRecords.addToSellOrders(order)

            val sortedBuyOrders = orderRecords.sortBuyOrder()

            for (bestBuyOrder in sortedBuyOrders) {
                if (order.remainingQuantity == 0L) {
                    break
                }
                if ((order.price <= bestBuyOrder.price) && (bestBuyOrder.remainingQuantity > 0)) {
                    val remainingQuantity = order.remainingQuantity

                    val orderExecutionPrice = order.price
                    val orderExecutionQuantity = min(order.remainingQuantity, bestBuyOrder.remainingQuantity)

                    val orderLog = OrderFilledLog(
                        orderExecutionQuantity,
                        orderExecutionPrice,
                        order.esopType,
                        order.userName,
                        bestBuyOrder.userName
                    )

                    bestBuyOrder.remainingQuantity = bestBuyOrder.remainingQuantity - orderExecutionQuantity
                    order.remainingQuantity = order.remainingQuantity - orderExecutionQuantity

                    updateStatus(bestBuyOrder)
                    updateStatus(order)

                    bestBuyOrder.orderFilledLogs.add(orderLog)
                    order.orderFilledLogs.add(orderLog)

                    updateOrderDetailsForSell(
                        order.userName,
                        remainingQuantity,
                        order.remainingQuantity,
                        bestBuyOrder,
                        order
                    )

                }
            }
        }

        userRecords.getUser(order.userName)?.orderList?.add(order)

        return mapOf("orderId" to order.orderID)
    }

    private fun updateStatus(order: Order) {
        if (order.remainingQuantity == 0L) {
            order.orderStatus = "COMPLETED"
            if (order.type == "BUY")
                orderRecords.removeFromBuyOrders(order)
            else
                orderRecords.removeFromSellOrders(order)
        } else {
            order.orderStatus = "PARTIAL"
        }
    }

    fun orderHistory(userName: String): Any {
        val userErrors = ArrayList<String>()
        if (!userRecords.checkIfUserExists(userName)) {
            errors["USER_DOES_NOT_EXISTS"]?.let { userErrors.add(it) }
            return mapOf("error" to userErrors)
        }
        val orderDetails = userRecords.getUser(userName)!!.orderList
        val orderHistory = ArrayList<History>()

        for (orders in orderDetails) {
            orderHistory.add(
                History(
                    orders.orderID,
                    orders.quantity,
                    orders.type,
                    orders.price,
                    orders.orderStatus,
                    orders.orderFilledLogs
                )
            )
        }

        return orderHistory
    }

}

