package com.hesab.hesabpaysdk

import com.hesab.hesabpaysdk.api.HesabPayApi
import com.hesab.hesabpaysdk.api.HesabPayApiClient
import com.hesab.hesabpaysdk.models.Environment

internal data class HesabPayConfig(
    val apiKey: String,
    val environment: Environment,
    val api: HesabPayApi
) {
    companion object {
        fun create(apiKey: String, environment: Environment): HesabPayConfig {
            val api = HesabPayApiClient.createApi(apiKey, environment)
            return HesabPayConfig(apiKey, environment, api)
        }
    }
}

