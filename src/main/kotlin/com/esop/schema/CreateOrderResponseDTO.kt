package com.esop.schema

data class CreateOrderResponseDTO(
    val orderId: Long,
    val quantity: Long,
    val price: Long,
    val type: String,
    val esopType: String?
)