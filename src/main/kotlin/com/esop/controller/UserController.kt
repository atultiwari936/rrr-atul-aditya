package com.esop.controller


import com.esop.HttpException
import com.esop.dto.AddInventoryDTO
import com.esop.dto.AddWalletDTO
import com.esop.dto.CreateOrderDTO
import com.esop.dto.UserCreationDTO
import com.esop.schema.Order
import com.esop.service.*
import com.fasterxml.jackson.core.JsonProcessingException
import io.micronaut.core.convert.exceptions.ConversionErrorException
import io.micronaut.http.*
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.validation.Validated
import io.micronaut.web.router.exceptions.UnsatisfiedBodyRouteException
import jakarta.inject.Inject
import javax.validation.ConstraintViolationException
import javax.validation.Valid


@Validated
@Controller("/user")
class UserController {

    @Inject
    lateinit var userService: UserService

    @Error(exception = HttpException::class)
    fun onHttpException(exception: HttpException): HttpResponse<Map<String, ArrayList<String?>>> {
        return HttpResponse.status<Map<String, ArrayList<String>>>(exception.status)
            .body(mapOf("errors" to arrayListOf(exception.message)))
    }

    @Error(exception = JsonProcessingException::class)
    fun onJSONProcessingExceptionError(exception: JsonProcessingException): HttpResponse<Map<String, ArrayList<String>>> {
        return HttpResponse.badRequest(mapOf("errors" to arrayListOf("Invalid JSON format")))
    }

    @Error(exception = UnsatisfiedBodyRouteException::class)
    fun onUnsatisfiedBodyRouteException(
        request: HttpRequest<Map<String, ArrayList<String>>>,
        exception: UnsatisfiedBodyRouteException
    ): HttpResponse<Map<String, List<*>>> {
        return HttpResponse.badRequest(mapOf("errors" to arrayListOf("request body missing")))
    }

    @Error(status = HttpStatus.NOT_FOUND, global = true)
    fun onRouteNotFound(): HttpResponse<Map<String, List<String>>> {
        return HttpResponse.badRequest(mapOf("errors" to arrayListOf("route not found")))
    }

    @Error(exception = ConversionErrorException::class)
    fun onConversionErrorException(exception: ConversionErrorException): HttpResponse<Map<String, List<String?>>> {
        return HttpResponse.badRequest(mapOf("errors" to arrayListOf(exception.message)))
    }

    @Error(exception = ConstraintViolationException::class)
    fun onConstraintViolationException(exception: ConstraintViolationException): HttpResponse<Map<String, List<String>>> {
        return HttpResponse.badRequest(mapOf("errors" to exception.constraintViolations.map { it.message }))
    }

    @Error(exception = RuntimeException::class)
    fun onRuntimeException(exception: RuntimeException): HttpResponse<Map<String, List<String?>>> {
        return HttpResponse.serverError(mapOf("errors" to arrayListOf(exception.message)))
    }

    @Post(uri = "/register", consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON])
    fun createUser(@Body @Valid userData: UserCreationDTO): HttpResponse<*> {
        val newUser = this.userService.registerUser(userData)
        if (newUser["error"] != null) {
            return HttpResponse.badRequest(newUser)
        }
        return HttpResponse.ok(newUser)
    }

    @Post(uri = "/{userName}/order", consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON])
    fun createOrder(userName: String, @Body @Valid body: CreateOrderDTO): Any? {
        var errorList = mutableListOf<String>()

        val orderType: String = body.type.toString().uppercase()
        var esopType = "NON_PERFORMANCE"

        if (orderType == "SELL") {
            esopType = body.esopType.toString().uppercase()
            if (esopType != "PERFORMANCE" && esopType != "NON_PERFORMANCE") {
                errorList.add("Invalid inventory type")
                return HttpResponse.ok(mapOf("errors" to errorList))
            }
        }

        val order = Order(body.quantity!!.toLong(), body.type.toString().uppercase(), body.price!!.toLong(), userName)
        if (orderType == "SELL") {
            order.esopType = esopType
            if (esopType == "PERFORMANCE")
                order.inventoryPriority = 1
        }
        errorList = UserService.orderCheckBeforePlace(order)
        if (errorList.size > 0) {
            return HttpResponse.badRequest(mapOf("errors" to errorList))
        }
        val userOrderOrErrors = OrderService.placeOrder(order)

        return if (userOrderOrErrors["orderId"] != null) {
            HttpResponse.ok(
                mapOf(
                    "orderId" to userOrderOrErrors["orderId"],
                    "quantity" to body.quantity,
                    "type" to body.type,
                    "price" to body.price
                )
            )
        } else {
            HttpResponse.badRequest(userOrderOrErrors)
        }
    }

    @Get(uri = "/{userName}/accountInformation", produces = [MediaType.APPLICATION_JSON])
    fun getAccountInformation(userName: String): HttpResponse<*> {
        val userData = this.userService.accountInformation(userName)

        if (userData["error"] != null) {
            return HttpResponse.badRequest(userData)
        }

        return HttpResponse.ok(userData)
    }

    @Post(
        uri = "{userName}/inventory",
        consumes = [MediaType.APPLICATION_JSON],
        produces = [MediaType.APPLICATION_JSON]
    )
    fun addEsopsToInventory(userName: String, @Body @Valid body: AddInventoryDTO): HttpResponse<*> {
        val newInventory = this.userService.addingInventory(body, userName)

        if (newInventory["error"] != null) {
            return HttpResponse.badRequest(newInventory)
        }
        return HttpResponse.ok(newInventory)
    }


    @Post(uri = "{userName}/wallet", consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON])
    fun addAmountToWallet(userName: String, @Body @Valid body: AddWalletDTO): HttpResponse<*> {
        val addedMoney = this.userService.addingMoney(body, userName)

        if (addedMoney["error"] != null) {
            return HttpResponse.badRequest(addedMoney)
        }
        return HttpResponse.ok(addedMoney)

    }

    @Get(uri = "/{userName}/orderHistory", produces = [MediaType.APPLICATION_JSON])
    fun getOrderHistory(userName: String): HttpResponse<*> {
        val orderHistoryData = OrderService.orderHistory(userName)
        if (orderHistoryData is Map<*, *>) {
            return HttpResponse.badRequest(orderHistoryData)
        }
        return HttpResponse.ok(orderHistoryData)
    }
}