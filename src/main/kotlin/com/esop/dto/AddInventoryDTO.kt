package com.esop.dto

import com.esop.constant.MAX_INVENTORY_CAPACITY
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern

@Introspected
class AddInventoryDTO @JsonCreator constructor(
    @JsonProperty("quantity")
    @field:NotNull(message = "The quantity field cannot be blank or missing.")
    @field:Min(1, message = "Quantity must be greater than zero")
    @field:Max(MAX_INVENTORY_CAPACITY, message = "Quantity cannot exceed the ${MAX_INVENTORY_CAPACITY.toString()} maximum inventory capacity.")
    var quantity: Long? = null,

    @JsonProperty("esopType")
    @field:Pattern(regexp = "^((?i)NON_PERFORMANCE|(?i)PERFORMANCE)$", message = "The value of esopType must either be NON PERFORMANCE or PERFORMANCE.")
    var esopType: String? = "NON_PERFORMANCE"
)
