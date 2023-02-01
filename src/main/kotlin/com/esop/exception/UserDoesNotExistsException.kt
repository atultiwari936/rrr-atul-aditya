package com.esop.exception

class UserDoesNotExistsException : Exception() {
    override fun toString(): String {
        return "User does not exists."
    }

}
