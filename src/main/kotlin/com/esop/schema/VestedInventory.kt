package com.esop.schema

class VestedInventory (
    private var vestInventory: Long = 0L,
    private var timestamp: Long = System.currentTimeMillis(),
    private var movedVestedInventory: Long = 0L,
    private var type: String)
{

    fun getTimeStamp():Long{
        return timestamp
    }

    fun addESOPsToInventory(inventory: Long){
        vestInventory += inventory
    }

    fun getVestInventory():Long{
        return vestInventory
    }

    fun getMovedVestedInventory():Long{
        return movedVestedInventory
    }

    fun setMovedVestedInventory(inventory: Long){
         movedVestedInventory = inventory
    }

}