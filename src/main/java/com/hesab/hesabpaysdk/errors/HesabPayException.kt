package com.hesab.hesabpaysdk.errors

sealed class HesabPayException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class NetworkException(message: String, cause: Throwable? = null) : HesabPayException(message, cause)
    class InvalidApiKeyException(message: String) : HesabPayException(message)
    class SessionCreationException(message: String, cause: Throwable? = null) : HesabPayException(message, cause)
    class UserCancellationException(message: String = "Payment was cancelled by user") : HesabPayException(message)
    class InvalidRequestException(message: String) : HesabPayException(message)
}

