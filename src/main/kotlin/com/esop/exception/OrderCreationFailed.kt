package com.esop.exception

class OrderCreationFailed : Exception() {
    override fun toString(): String {
        return "Order creation failed."
    }
}
