package com.esop.service

class OrderCreationFailed : Exception() {
    override fun toString(): String {
        return "Order creation failed."
    }
}
