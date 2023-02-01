package com.esop.schema

class InventoryLimitExceededException : Exception() {
    public override fun toString(): String {
        return "Inventory Limit exceeded";
    }
}
