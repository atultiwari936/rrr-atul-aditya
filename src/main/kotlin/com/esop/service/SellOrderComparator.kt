package com.esop.service

import com.esop.schema.Order

class SellOrderComparator {
    companion object : Comparator<Order> {
        override fun compare(o1: Order, o2: Order): Int {
            if (o1.price != o2.price) {
                if (o1.price > o2.price) {
                    return 1
                } else {
                    return -1
                }
            } else {
                if (o1.orderID > o2.orderID) {
                    return 1
                } else {
                    return -1
                }
            }
        }
    }
}