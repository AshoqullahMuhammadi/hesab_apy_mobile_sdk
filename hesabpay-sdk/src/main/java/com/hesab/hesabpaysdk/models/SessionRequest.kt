package com.hesab.hesabpaysdk.models

import kotlinx.serialization.Serializable

@Serializable
data class SessionRequest(
    val email: String,
    val items: List<HesabPayItem>,
    val success_url: String,
    val failure_url: String
)

