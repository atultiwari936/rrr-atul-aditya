package com.esop

import io.micronaut.http.HttpStatus

open class HttpException(val status: HttpStatus, message: String): RuntimeException(message)

//class InventoryLimitExceededException: HttpException(HttpStatus.BAD_REQUEST, "Inventory Limit exceeded")

//class WalletLimitExceededException: HttpException(HttpStatus.BAD_REQUEST, "Wallet Limit exceeded")

//class InsufficientFundException: HttpException(HttpStatus.BAD_REQUEST, "Insufficient funds.")

//class InsufficientInventoryTypeException(type :String): HttpException(HttpStatus.BAD_REQUEST, "Insufficient ${type.toLowerCase()} inventory.")