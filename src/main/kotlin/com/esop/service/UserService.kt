package com.esop.service

import com.esop.errors
import com.esop.dto.AddInventoryDTO
import com.esop.dto.AddWalletDTO
import com.esop.dto.UserCreationDTO
import com.esop.schema.User
import jakarta.inject.Singleton
import com.esop.schema.Order

@Singleton
class UserService {
    companion object {
        val emailList = mutableSetOf<String>()
        val phoneNumberList = mutableSetOf<String>()
        var userList = HashMap<String, User>()

        fun orderCheckBeforePlace(order: Order): MutableList<String> {
            val errorList = mutableListOf<String>()
            val user = userList[order.userName]!!
            val wallet = user.userWallet
            val nonPerformanceInventory = user.userNonPerfInventory

            if(!userList.containsKey(order.userName)){
                errorList.add("User doesn't exist.")
                return errorList
            }

            if(order.type == "BUY"){
                nonPerformanceInventory.assertInventoryWillNotOverflowOnAdding(order.quantity)

                val response = user.userWallet.moveMoneyFromFreeToLockedState(order.price*order.quantity)
                if (response != "SUCCESS"){
                    errorList.add(response)
                }
            }
            else if(order.type == "SELL"){
                wallet.assertWalletWillNotOverflowOnAdding(order.price * order.quantity)

                if(order.esopType == "PERFORMANCE"){
                    val response = user.userPerformanceInventory.moveESOPsFromFreeToLockedState(order.quantity)
                    if ( response != "SUCCESS" ){
                        errorList.add(response)
                    }
                }else if(order.esopType == "NON_PERFORMANCE"){
                    val response = user.userNonPerfInventory.moveESOPsFromFreeToLockedState(order.quantity)
                    if ( response != "SUCCESS" ){
                        errorList.add(response)
                    }
                }
            }
            return errorList
        }
    }

    private fun checkUsername(searchValue: String): Boolean
    {
        return userList.contains(searchValue)
    }

    private fun checkPhoneNumber(userNumber: MutableSet<String>, searchValue: String): Boolean
    {
        return userNumber.contains(searchValue)
    }

    private fun checkEmail(userEmail: MutableSet<String>, searchValue: String): Boolean
    {
        return userEmail.contains(searchValue)
    }


    private fun validateUserDetails(userData: UserCreationDTO): List<String>
    {
        val errorList = mutableListOf<String>()

        if(checkUsername(userData.username!!)){
            errorList.add(errors["USERNAME_EXISTS"].toString())
        }
        if(checkEmail(emailList, userData.email!!)){
            errorList.add(errors["EMAIL_EXISTS"].toString())
        }
        if(checkPhoneNumber(phoneNumberList, userData.phoneNumber!!)){
            errorList.add(errors["PHONENUMBER_EXISTS"].toString())
        }

        return errorList
    }

    fun registerUser(userData: UserCreationDTO): Map<String,Any>
    {
        val errors = validateUserDetails(userData)
        if(errors.isNotEmpty()) {
            return mapOf("error" to errors)
        }
        val user = User(
            userData.firstName!!.trim(),
            userData.lastName!!.trim(),
            userData.phoneNumber!!,
            userData.email!!,
            userData.username!!
        )
        userList[userData.username!!] = user
        emailList.add(userData.email!!)
        phoneNumberList.add(userData.phoneNumber!!)
        return mapOf(
            "firstName" to user.firstName,
            "lastName" to user.lastName,
            "phoneNumber" to user.phoneNumber,
            "email" to user.email,
            "username" to user.username
        )
    }

    fun accountInformation(userName: String): Map<String, Any?>
    {
        val errorList = mutableListOf<String>()

        if ( !checkUsername(userName) ){
            errorList.add(errors["USER_DOES_NOT_EXISTS"].toString())
        }

        if( errorList.size > 0 ){
            return mapOf("error" to errorList)
        }
        val user = userList[userName]!!

        return mapOf(
            "firstName" to user.firstName,
            "lastName" to user.lastName,
            "phoneNumber" to user.phoneNumber,
            "email" to user.email,
            "wallet" to mapOf(
                "free" to user.userWallet.getFreeMoney(),
                "locked" to user.userWallet.getLockedMoney()
            ),
            "inventory" to arrayListOf<Any>(
                mapOf(
                    "type" to "PERFORMANCE",
                    "free" to user.userPerformanceInventory.getFreeInventory(),
                    "locked" to user.userPerformanceInventory.getLockedInventory()
                ),
                mapOf(
                    "type" to "NON_PERFORMANCE",
                    "free" to user.userNonPerfInventory.getFreeInventory(),
                    "locked" to user.userNonPerfInventory.getLockedInventory()
                )
            )
        )
    }


    fun addEsopsToInventory(inventoryData: AddInventoryDTO, userName: String): Map<String, Any>
    {
        val errorList = mutableListOf<String>()

        if ( inventoryData.esopType.toString().uppercase() != "NON_PERFORMANCE" && inventoryData.esopType.toString().uppercase() != "PERFORMANCE" ){
            errorList.add(errors["INVALID_TYPE"].toString())
        }
        else if ( !checkUsername(userName) ){
            errorList.add(errors["USER_DOES_NOT_EXISTS"].toString())
        }

        if( errorList.size > 0 ) {
            return mapOf("error" to errorList)
        }
        return mapOf("message" to userList[userName]!!.addToInventory(inventoryData))
    }

    fun addMoneyToWallet(walletData: AddWalletDTO, userName: String): Map<String, Any>
    {
        val errorList = mutableListOf<String>()

        if ( !checkUsername(userName) ){
            errorList.add(errors["USER_DOES_NOT_EXISTS"].toString())
        }

        if( errorList.size > 0 ) {
            return mapOf("error" to errorList)
        }

        return mapOf("message" to userList[userName]!!.addToWallet(walletData))
    }
}