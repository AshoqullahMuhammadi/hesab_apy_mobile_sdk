package com.hesab.hesabpaysdk.models

data class PaymentIntent(
    val email: String,
    val items: List<HesabPayItem>,
    val successUrl: String,
    val failureUrl: String
) {
    init {
        require(email.isNotBlank()) { "Email cannot be blank" }
        require(items.isNotEmpty()) { "Items list cannot be empty" }
        require(successUrl.isNotBlank()) { "Success URL cannot be blank" }
        require(failureUrl.isNotBlank()) { "Failure URL cannot be blank" }
        require(items.all { it.price > 0 }) { "All item prices must be greater than 0" }
    }
}

