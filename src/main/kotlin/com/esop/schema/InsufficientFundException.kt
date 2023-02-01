package com.esop.schema

class InsufficientFundException : Exception() {
    override fun toString(): String {
        return "Insufficient funds."
    }
}
