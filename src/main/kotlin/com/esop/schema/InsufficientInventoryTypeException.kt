package com.esop.schema

class InsufficientInventoryTypeException(type: String) : Exception() {
    fun InsufficientInventoryTypeException(type: String){
        Exception("Insufficient " + type.toLowerCase() + " inventory.")
    }

    override fun toString(): String {
        return super.message!!
    }
}
