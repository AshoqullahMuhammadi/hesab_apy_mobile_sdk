package com.hesab.hesabpaysdk.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SessionResponse(
    @SerialName("url")
    val paymentUrl: String,
    @SerialName("session_id")
    val sessionId: String? = null
)

