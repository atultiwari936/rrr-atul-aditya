package com.esop.schema

import com.esop.constant.MAX_INVENTORY_CAPACITY
import com.esop.custom.exceptions.InsufficientNonPerformanceInventoryException
import com.esop.custom.exceptions.InsufficientPerformanceInventoryException
import com.esop.custom.exceptions.InventoryLimitExceededException

class Inventory(
    private var freeInventory: Long = 0L,
    private var lockedInventory: Long = 0L,
    private var type: String
) {

    private fun totalESOPQuantity(): Long {
        return freeInventory + lockedInventory
    }

    private fun willInventoryOverflowOnAdding(quantity: Long): Boolean {
        return quantity + totalESOPQuantity() > MAX_INVENTORY_CAPACITY
    }

    fun checkIfInventoryWillNotOverflowOnAdding(quantity: Long) {
        if (willInventoryOverflowOnAdding(quantity)) throw InventoryLimitExceededException("Inventory limit exceeded.")
    }

    fun addESOPsToInventory(esopsToBeAdded: Long) {
        checkIfInventoryWillNotOverflowOnAdding(esopsToBeAdded)

        this.freeInventory = this.freeInventory + esopsToBeAdded
    }

    fun moveESOPsFromFreeToLockedState(esopsToBeLocked: Long) {
        if (this.freeInventory < esopsToBeLocked) {
            if (type == "PERFORMANCE") throw InsufficientPerformanceInventoryException("Insufficient performance inventory.")
            else if (type == "NON_PERFORMANCE") throw InsufficientNonPerformanceInventoryException("Insufficient non-performance inventory.")
        }
        this.freeInventory = this.freeInventory - esopsToBeLocked
        this.lockedInventory = this.lockedInventory + esopsToBeLocked
    }

    fun getFreeInventory(): Long {
        return freeInventory
    }

    fun getLockedInventory(): Long {
        return lockedInventory
    }

    fun removeESOPsFromLockedState(esopsToBeRemoved: Long) {
        this.lockedInventory = this.lockedInventory - esopsToBeRemoved
    }
}