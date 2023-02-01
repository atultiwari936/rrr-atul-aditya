package com.esop.exception

class InsufficientNonPerformanceInventoryException : Exception(){
    override fun toString(): String {
        return "Insufficient non-performance inventory."
    }
}