package com.esop.dto

import com.esop.constant.MAX_INVENTORY_CAPACITY
import com.esop.constant.MAX_WALLET_CAPACITY
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.*


@Introspected
class CreateOrderDTO @JsonCreator constructor(

    @field:NotBlank(message = "The type field cannot be blank or missing.")
    @field:Pattern(regexp = "^((?i)BUY|(?i)SELL)$", message = "Invalid Type: should be one of BUY or SELL")
    var type: String? = null,

    @field:NotNull(message = "The quantity field cannot be blank or missing.")
    @field:Min(1, message = "Quantity must be greater than zero")
    @field:Max(MAX_INVENTORY_CAPACITY, message = "Quantity cannot exceed the ${MAX_INVENTORY_CAPACITY.toString()} maximum inventory capacity.")
    var quantity: Long? = null,

    @JsonProperty("price")
    @field:NotNull(message = "The price field cannot be blank or missing.")
    @field:Min(1, message = "Price must be greater than zero")
    @field:Digits(integer = 9, fraction = 0, message = "Price cannot exceed the ${MAX_WALLET_CAPACITY.toString()} maximum inventory capacity.")
    var price: Long? = null,

    @JsonProperty("esopType")
    @field:Pattern(regexp = "^((?i)NON_PERFORMANCE|(?i)PERFORMANCE)$", message = "The value of esopType must either be NON PERFORMANCE or PERFORMANCE.")
    var esopType: String? = "NON_PERFORMANCE"
)
