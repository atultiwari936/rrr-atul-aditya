package com.esop.exception

class InventoryLimitExceededException : Exception() {
    override fun toString(): String {
        return "Inventory Limit exceeded";
    }
}
