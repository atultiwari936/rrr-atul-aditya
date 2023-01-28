package com.esop.controller

import com.esop.InventoryLimitExceededException
import com.esop.WalletLimitExceededException
import com.esop.dto.AddInventoryDTO
import com.esop.dto.AddWalletDTO
import com.esop.dto.CreateOrderDTO
import com.esop.dto.UserCreationDTO
import com.esop.schema.Order
import com.esop.service.*
import com.fasterxml.jackson.core.JsonProcessingException
import io.micronaut.core.convert.exceptions.ConversionErrorException
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Error
import io.micronaut.http.annotation.Post
import io.micronaut.http.server.exceptions.HttpStatusHandler
import io.micronaut.validation.Validated
import jakarta.inject.Inject
import javax.validation.Valid
import javax.validation.Validator


@Validated
@Controller("/user")
class UserController {

    @Inject
    lateinit var validator: Validator

    @Inject
    lateinit var orderService: OrderService

    @Inject
    lateinit var userService: UserService

    fun <T> checkValidationError(input: T): List<String> {
        return validator.validate(input).map { it.message }
    }

    @Error(exception = JsonProcessingException::class)
    fun onJSONProcessingExceptionError(): HttpResponse<Map<String, ArrayList<String>>> {
        return HttpResponse.badRequest(mapOf("errors" to arrayListOf("JSON processing error")))
    }

    @Error(exception = ConversionErrorException::class)
    fun onConversionErrorException(request: HttpRequest<*>, ex: ConversionErrorException): HttpResponse<Map<String, ArrayList<*>>>  {
        println(request.path)
        return HttpResponse.badRequest(mapOf("errors" to arrayListOf(ex.message)))
    }


    @Post(uri="/register", consumes = [MediaType.APPLICATION_JSON],produces=[MediaType.APPLICATION_JSON])
     fun register(@Body @Valid userData: UserCreationDTO): HttpResponse<*> {
        val newUser = this.userService.registerUser(userData)
        if(newUser["error"] != null) {
            return HttpResponse.badRequest(newUser)
        }
        return HttpResponse.ok(newUser)
    }

    @Post(uri="/{userName}/order", consumes = [MediaType.APPLICATION_JSON],produces=[MediaType.APPLICATION_JSON])
    fun order(userName: String, @Body @Valid body: CreateOrderDTO): Any? {
        var errorList = mutableListOf<String>()

        val orderType: String = body.type.toString().uppercase()
        var inventoryType = "NON_PERFORMANCE"

        if(orderType == "SELL"){
            inventoryType = body.inventoryType.toString().uppercase()
            if(inventoryType != "PERFORMANCE" && inventoryType != "NON_PERFORMANCE"){
                errorList.add("Invalid inventory type")
                return HttpResponse.ok(mapOf("errors" to errorList))
            }
        }

        val order = Order(body.quantity!!.toLong(),body.type.toString().uppercase(),body.price!!.toLong(),userName)
        if(orderType == "SELL"){
            order.inventoryType = inventoryType
            if(inventoryType == "PERFORMANCE")
                order.inventoryPriority = 1
        }
        errorList = UserService.orderCheckBeforePlace(order)
        if(errorList.size > 0){
            return HttpResponse.badRequest(mapOf("errors" to errorList))
        }
        val userOrderOrErrors = OrderService.placeOrder(order)

        if (userOrderOrErrors["orderId"] != null) {
            return HttpResponse.ok(mapOf(
                "orderId" to userOrderOrErrors["orderId"],
                "quantity" to body.quantity,
                "type" to body.type,
                "price" to body.price
            ))
        }else{
            return HttpResponse.badRequest(userOrderOrErrors)
        }
    }

    @Get(uri = "/{userName}/accountInformation", produces = [MediaType.APPLICATION_JSON])
    fun getAccountInformation(userName: String): HttpResponse<*> {
        val userData = this.userService.accountInformation(userName)

        if(userData["error"] != null) {
            return HttpResponse.badRequest(userData)
        }

        return HttpResponse.ok(userData)
    }

    @Post(uri = "{userName}/inventory", consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON])
    fun addInventory(userName: String, @Body @Valid body: AddInventoryDTO): HttpResponse<*>{
        val validationErrors = checkValidationError(body)

        if (validationErrors.isNotEmpty()) {
            return HttpResponse.badRequest(mapOf("errors" to validationErrors))
        }

        val newInventory = this.userService.addingInventory(body,userName)

        if(newInventory["error"] != null) {
            return HttpResponse.badRequest(newInventory)
        }
        return HttpResponse.ok(newInventory)
    }


    @Post(uri = "{userName}/wallet", consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON])
    fun addWallet(userName: String, @Body @Valid body: AddWalletDTO) :HttpResponse<*> {
        val validationErrors = checkValidationError(body)

        if (validationErrors.isNotEmpty()) {
            return HttpResponse.badRequest(mapOf("errors" to validationErrors))
        }

        val addedMoney=this.userService.addingMoney(body,userName)

        if(addedMoney["error"] != null) {
            return HttpResponse.badRequest(addedMoney)
        }
        return HttpResponse.ok(addedMoney)

    }

    @Get(uri = "/{userName}/orderHistory", produces = [MediaType.APPLICATION_JSON])
    fun orderHistory(userName: String): HttpResponse<*> {
        val orderHistoryData = OrderService.orderHistory(userName)
        if(orderHistoryData is Map<*, *>){
            return HttpResponse.badRequest(orderHistoryData)
        }
        return HttpResponse.ok(orderHistoryData)
    }
}