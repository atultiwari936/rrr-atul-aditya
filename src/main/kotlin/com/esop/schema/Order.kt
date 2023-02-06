package com.esop.schema

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
    var inventoryPriority: Int = 0

    init {
        if (isTypeSellAndEsopTypePerformance()) {
            inventoryPriority = 1
        } else if (isTypeSellAndEsopTypeNonPerformance()) {
            inventoryPriority = 2
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