package com.esop.schema

import com.esop.constant.MAX_INVENTORY_CAPACITY

class Inventory(
    private var freeInventory: Long = 0L ,
    private var lockedInventory: Long = 0L,
    private var type: String
) {

    private fun totalESOPQuantity(): Long {
        return freeInventory + lockedInventory
    }

    private fun willInventoryOverflowOnAdding(quantity: Long): Boolean {
        return quantity + totalESOPQuantity() > MAX_INVENTORY_CAPACITY
    }

    fun checkInventoryWillNotOverflowOnAdding(quantity: Long) {
        if (willInventoryOverflowOnAdding(quantity)) throw InventoryLimitExceededException()
    }

    fun addESOPsToInventory(esopsToBeAdded: Long) {
        checkInventoryWillNotOverflowOnAdding(esopsToBeAdded)

        this.freeInventory = this.freeInventory + esopsToBeAdded
    }

    fun moveESOPsFromFreeToLockedState(esopsToBeLocked: Long) {
        if (this.freeInventory < esopsToBeLocked){
            if (type == "PERFORMANCE") throw InsufficientPerformanceInventoryException()
            else if(type == "NON_PERFORMANCE") throw InsufficientNonPerformanceInventoryException()
        }
        this.freeInventory = this.freeInventory - esopsToBeLocked
        this.lockedInventory = this.lockedInventory + esopsToBeLocked
    }

    fun getFreeInventory():Long{
        return freeInventory
    }

    fun getLockedInventory():Long{
        return lockedInventory
    }

    fun removeESOPsFromLockedState( esopsToBeRemoved: Long){
        this.lockedInventory = this.lockedInventory - esopsToBeRemoved
    }
}