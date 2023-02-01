package com.esop.schema

class WalletLimitExceededException : Exception() {
    override fun toString(): String {
        return "Wallet Limit exceeded."
    }
}
