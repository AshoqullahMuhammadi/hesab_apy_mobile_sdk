package com.hesab.hesabpaysdk.api

import com.hesab.hesabpaysdk.models.Environment
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

object HesabPayApiClient {
    private const val SANDBOX_BASE_URL = "https://api-sandbox.hesab.com/api/v1/payment/"
    private const val PRODUCTION_BASE_URL = "https://api.hesab.com/api/v1/payment/"

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = false
    }

    private val contentType = "application/json".toMediaType()

    fun createApi(apiKey: String, environment: Environment): HesabPayApi {
        val baseUrl = when (environment) {
            Environment.SANDBOX -> SANDBOX_BASE_URL
            Environment.PRODUCTION -> PRODUCTION_BASE_URL
        }

        val apiKeyInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val newRequest = originalRequest.newBuilder()
                .header("Authorization", "API-KEY $apiKey")
                .header("Content-Type", "application/json")
                .build()
            chain.proceed(newRequest)
        }

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (environment == Environment.SANDBOX) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()

        return retrofit.create(HesabPayApi::class.java)
    }
}

