package com.hesab.hesabpaysdk.models

import kotlinx.serialization.Serializable

@Serializable
data class HesabPayItem(
    val id: String,
    val name: String,
    val price: Double
)

