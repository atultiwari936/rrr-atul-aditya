package com.esop.schema

class InsufficientPerformanceInventoryException : Exception() {
    override fun toString(): String {
        return "Insufficient performance inventory."
    }
}
