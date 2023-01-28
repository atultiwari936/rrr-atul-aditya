package com.esop.schema

import com.esop.dto.AddInventoryDTO
import com.esop.dto.AddWalletDTO

class User ( var firstName: String,
             var lastName: String,
             var phoneNumber: String,
             var email: String,
             var username: String){
    val userWallet: Wallet = Wallet()
    val userNonPerfInventory: Inventory = Inventory(type = "NON_PERFORMANCE")
    val userPerformanceInventory: Inventory = Inventory(type = "PERFORMANCE")
    val uservestedInventories: ArrayList<VestedInventory> = ArrayList<VestedInventory>()
//    val orderList: ArrayList<Order> = ArrayList<Order>()
    val orderList: ArrayList<History> = ArrayList<History>()

    fun addToWallet(walletData: AddWalletDTO): String {
        userWallet.addMoneyToWallet(walletData.price!!)
        return "${walletData.price} amount added to account."
    }
    fun addToInventory(inventoryData: AddInventoryDTO): String {
        if(inventoryData.inventoryType.toString().uppercase() == "NON_PERFORMANCE") {
            var vestedInventory = VestedInventory(vestInventory =  inventoryData.quantity!!)
            uservestedInventories.add(vestedInventory)
            return "${inventoryData.quantity} Vested ESOPs added to account."
        }else if( inventoryData.inventoryType.toString().uppercase() == "PERFORMANCE" ){
            userPerformanceInventory.addESOPsToInventory(inventoryData.quantity!!)
            return "${inventoryData.quantity} Performance ESOPs added to account."
        }
        return "None"
    }

    fun addToNonPerfInventory(inventory: Long){
        userNonPerfInventory.addESOPsToInventory(inventory)
    }

}