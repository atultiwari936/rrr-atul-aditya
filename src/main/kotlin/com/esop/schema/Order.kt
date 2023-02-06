package com.esop.schema

import com.esop.schema.InvenrtoryPriority.*

enum class InvenrtoryPriority(val priority: Int){
    NONE(0),
    PERFORMANCE(1),
    NON_PERFORMANCE(1)
}

class Order(
    var orderID: Long,
    var quantity: Long,
    var type: String,
    var price: Long,
    var userName: String,
    var esopType: String?
) {
    var timeStamp = System.currentTimeMillis()
    var remainingQuantity: Long = quantity
    var status: String = "PENDING" // COMPLETED, PARTIAL, PENDING
    var executionHistory: MutableList<OrderFilledLog> = mutableListOf()
    var inventoryPriority:InvenrtoryPriority = NONE

    init {
        if (isTypeSellAndEsopTypePerformance()) {
            inventoryPriority = PERFORMANCE
        } else if (isTypeSellAndEsopTypeNonPerformance()) {
            inventoryPriority = NON_PERFORMANCE
        }
    }

    private fun isTypeSellAndEsopTypePerformance() = type == "SELL" && esopType == "PERFORMANCE"

    private fun isTypeSellAndEsopTypeNonPerformance() = type == "SELL" && esopType == "NON_PERFORMANCE"

    fun isComplete(): Boolean {
        return status == "COMPLETED"
    }

    fun updateOrderStatus(executedOrder: OrderFilledLog) {
        if (executedOrder.quantity == remainingQuantity) {
            status = "COMPLETED"
        } else if (executedOrder.quantity < remainingQuantity) {
            status = "PARTIAL"
        }
    }

    fun subtractFromRemainingQuantity(quantityToBeUpdated: Long) {
        remainingQuantity -= quantityToBeUpdated
    }

    fun addOrderExecutionHistory(executedOrder: OrderFilledLog) {
        executionHistory.add(executedOrder)
    }

    fun isBuyOrder(): Boolean {
        return this.type == "BUY"
    }

    fun isSellOrder(): Boolean {
        return this.type == "SELL"
    }
}