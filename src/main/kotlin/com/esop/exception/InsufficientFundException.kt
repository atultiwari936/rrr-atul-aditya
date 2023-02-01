package com.esop.exception

class InsufficientFundException : Exception() {
    override fun toString(): String {
        return "Insufficient funds."
    }
}
