package com.hesab.hesabpaysdk.api

import com.hesab.hesabpaysdk.models.SessionRequest
import com.hesab.hesabpaysdk.models.SessionResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface HesabPayApi {
    @POST("create-session")
    suspend fun createSession(@Body request: SessionRequest): SessionResponse
}

