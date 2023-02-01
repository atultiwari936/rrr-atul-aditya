package com.esop.exception

class InsufficientPerformanceInventoryException : Exception() {
    override fun toString(): String {
        return "Insufficient performance inventory."
    }
}
