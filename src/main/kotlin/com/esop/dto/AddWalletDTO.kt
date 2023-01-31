package com.esop.dto

import com.esop.constant.MAX_WALLET_CAPACITY
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.Digits
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull


@Introspected
class AddWalletDTO @JsonCreator constructor(
    @JsonProperty("amount")
    @field:NotNull(message = "The amount field cannot be blank or missing.")
    @field:Digits(integer = 9, fraction = 0, message = "Amount cannot exceed the ${MAX_WALLET_CAPACITY.toString()} maximum wallet capacity.")
    @field:Min(1, message = "Amount must be greater than zero")
    var price: Long? = null,
)
