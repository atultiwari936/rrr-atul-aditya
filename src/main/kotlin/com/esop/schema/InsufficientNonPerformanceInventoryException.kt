package com.esop.schema

class InsufficientNonPerformanceInventoryException : Exception(){
    override fun toString(): String {
        return "Insufficient non-performance inventory."
    }
}