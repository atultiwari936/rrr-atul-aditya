package com.esop.schema

class Order(
    var orderID: Long,
    var quantity: Long,
    var type: String,
    var price: Long,
    var userName: String,
    var esopType: String = "NON_PERFORMANCE"
)
{
    var timeStamp = System.currentTimeMillis()
    var currentQuantity: Long = 0
    var remainingQuantity: Long = 0
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
    fun orderAvailable():Boolean{
        return orderStatus != "COMPLETED"
    }

}