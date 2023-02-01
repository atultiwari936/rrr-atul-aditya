package com.esop.service

class UserDoesNotExistsException : Exception() {
    override fun toString(): String {
        return "User does not exists."
    }

}
