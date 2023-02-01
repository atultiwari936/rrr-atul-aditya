package com.esop.schema

class InventoryLimitExceededException : Exception() {
    override fun toString(): String {
        return "Inventory Limit exceeded";
    }
}
