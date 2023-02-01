package com.esop.schema

class Order(
    var orderID: Long,
    var quantity: Long,
    var type: String,
    var price: Long,
    var userName: String,
    var esopType: String?
)
{
    var timeStamp = System.currentTimeMillis()
    var remainingQuantity: Long = quantity
    var orderStatus: String = "PENDING" // COMPLETED, PARTIAL, PENDING
    var orderFilledLogs: MutableList<OrderFilledLog> = mutableListOf()
    var inventoryPriority: Int = 0

    init {
        if(typeIsSellForPerformance()){
            inventoryPriority = 1
        }else if (typeIsSellForNonPerformance()){
            inventoryPriority = 2
        }
    }

    private fun typeIsSellForPerformance() = type == "SELL" && esopType == "PERFORMANCE"
    private fun typeIsSellForNonPerformance() = type == "SELL" && esopType == "NON_PERFORMANCE"
    fun isComplete():Boolean{
        return orderStatus == "COMPLETED"
    }

    fun updateOrderStatus(executedOrder: OrderFilledLog){
        if(executedOrder.quantity == remainingQuantity){
            orderStatus = "COMPLETED"
        }else if(executedOrder.quantity < remainingQuantity){
            orderStatus = "PARTIAL"
        }
    }

    fun updateRemainingQuantityBySubtractingOrderExecutedQuantity(quantityToBeUpdated: Long){
        remainingQuantity -= quantityToBeUpdated
    }
    fun addOrderFilledLogs(executedOrder :OrderFilledLog){
        orderFilledLogs.add(executedOrder)
    }

    fun isBuyOrder(): Boolean {
        return this.type == "BUY"
    }

    fun isSellOrder(): Boolean{
        return this.type == "SELL"
    }
}