package com.esop

import io.micronaut.http.HttpStatus

open class HttpException(val status: HttpStatus, message: String): RuntimeException(message)

class InventoryLimitExceededException: HttpException(HttpStatus.BAD_REQUEST, "The inventory limit has been exceeded.")

class WalletLimitExceededException: HttpException(HttpStatus.BAD_REQUEST, "The wallet Limit has been exceeded.")