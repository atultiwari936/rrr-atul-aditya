package com.esop.repository

import com.esop.schema.Order
import jakarta.inject.Singleton


@Singleton
class OrderRecords {

    private val sellOrders = mutableListOf<Order>()
    private val buyOrders = mutableListOf<Order>()
    private var orderId = 1L

    @Synchronized
    fun generateOrderId(): Long{
        return orderId++
    }

    fun addTobuyOrders(order: Order) {
        buyOrders.add(order)
    }

    fun removeFromBuyOrders(order: Order) {
        buyOrders.remove(order)
    }

    fun removeFromSellOrders(order: Order) {
        sellOrders.remove(order)
    }

    fun addToSellOrders(order: Order) {
        sellOrders.add(order)
    }

    fun getSellOrders():List<Order>{
        return sellOrders
    }

    fun getBuyOrders():List<Order>{
        return buyOrders
    }

    fun sortBuyOrder(): List<Order> {
        return buyOrders.sortedWith(compareByDescending<Order> { it.price }.thenBy { it.timeStamp })
    }


    fun sortSellOrder(): List<Order> {
        return sellOrders.sortedWith(object : Comparator<Order> {
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

}
