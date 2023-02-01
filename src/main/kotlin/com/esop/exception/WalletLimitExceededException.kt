package com.esop.exception

class WalletLimitExceededException : Exception() {
    override fun toString(): String {
        return "Wallet Limit exceeded."
    }
}
