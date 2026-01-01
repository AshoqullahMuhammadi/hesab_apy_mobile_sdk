package com.hesab.hesabpaysdk

import android.app.Activity
import android.content.Intent

import com.hesab.hesabpaysdk.errors.HesabPayException
import retrofit2.HttpException
import com.hesab.hesabpaysdk.models.Environment
import com.hesab.hesabpaysdk.models.HesabPayItem
import com.hesab.hesabpaysdk.models.HesabPayResult
import com.hesab.hesabpaysdk.models.PaymentIntent
import com.hesab.hesabpaysdk.models.SessionRequest
import com.hesab.hesabpaysdk.ui.HesabPayWebViewActivity
import com.hesab.hesabpaysdk.utils.PaymentUrlStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object HesabPay {
    @Volatile
    private var config: HesabPayConfig? = null

    @Volatile
    private var callback: HesabPayCallback? = null

    /**
     * Initialize the HesabPay SDK with API key and environment.
     * This method is idempotent - calling it multiple times will update the configuration.
     *
     * @param apiKey Your HesabPay API key
     * @param environment SANDBOX for testing, PRODUCTION for live payments
     */
    fun initialize(apiKey: String, environment: Environment) {
        require(apiKey.isNotBlank()) { "API key cannot be blank" }
        config = HesabPayConfig.create(apiKey, environment)
    }

    /**
     * Create a payment intent with customer email, items, and callback URLs.
     * This method does not open any UI - use [open] to launch the payment flow.
     *
     * @param email Customer email address
     * @param items List of items to purchase
     * @param successUrl URL to redirect to on successful payment
     * @param failureUrl URL to redirect to on failed/cancelled payment
     * @return PaymentIntent object
     */
    fun createPaymentIntent(
        email: String,
        items: List<HesabPayItem>,
        successUrl: String,
        failureUrl: String
    ): PaymentIntent {
        return PaymentIntent(
            email = email,
            items = items,
            successUrl = successUrl,
            failureUrl = failureUrl
        )
    }

    /**
     * Open the payment flow. This will:
     * 1. Create a payment session via HesabPay API
     * 2. Launch WebView Activity with the payment URL
     * 3. Handle redirects and call the callback with results
     *
     * @param activity The calling Activity
     * @param intent PaymentIntent created with [createPaymentIntent]
     * @param callback Callback to receive payment results
     */
    fun open(activity: Activity, intent: PaymentIntent, callback: HesabPayCallback) {
        val currentConfig = config
        requireNotNull(currentConfig) {
            "HesabPay SDK not initialized. Call HesabPay.initialize() first."
        }

        this.callback = callback

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val sessionRequest = SessionRequest(
                    email = intent.email,
                    items = intent.items,
                    success_url = intent.successUrl,
                    failure_url = intent.failureUrl
                )

                val response = currentConfig.api.createSession(sessionRequest)

                withContext(Dispatchers.Main) {
                    val paymentUrl = response.paymentUrl
                    // Store URLs for deep link handling
                    PaymentUrlStore.storeUrls(activity, intent.successUrl, intent.failureUrl)
                    launchWebViewActivity(activity, paymentUrl, intent.successUrl, intent.failureUrl)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    handleError(e, currentConfig.environment)
                }
            }
        }
    }

    private fun launchWebViewActivity(
        activity: Activity,
        paymentUrl: String,
        successUrl: String,
        failureUrl: String
    ) {
        val intent = Intent(activity, HesabPayWebViewActivity::class.java).apply {
            putExtra(HesabPayWebViewActivity.EXTRA_PAYMENT_URL, paymentUrl)
            putExtra(HesabPayWebViewActivity.EXTRA_SUCCESS_URL, successUrl)
            putExtra(HesabPayWebViewActivity.EXTRA_FAILURE_URL, failureUrl)
        }
        activity.startActivity(intent)
    }

    internal fun handleResult(result: HesabPayResult) {
        val currentCallback = callback
        callback = null // Clear callback after use

        when (result) {
            is HesabPayResult.Success -> currentCallback?.onSuccess(result)
            is HesabPayResult.Failure -> currentCallback?.onFailure(result)
        }
    }

    private fun handleError(error: Throwable, environment: com.hesab.hesabpaysdk.models.Environment) {
        val failureResult = when (error) {
            is HesabPayException -> HesabPayResult.Failure(
                reason = error.message,
                rawData = emptyMap()
            )
            is HttpException -> {
                val errorMessage = when (error.code()) {
                    401 -> "Invalid API key. Please check your API key."
                    400 -> "Invalid request. Please check your payment details."
                    500 -> "Server error. Please try again later."
                    else -> "Network error: ${error.message}"
                }
                HesabPayResult.Failure(
                    reason = errorMessage,
                    rawData = mapOf("http_code" to error.code().toString())
                )
            }
            is java.net.UnknownHostException -> HesabPayResult.Failure(
                reason = "Network error: Unable to connect to server. Please check your internet connection.",
                rawData = emptyMap()
            )
            else -> HesabPayResult.Failure(
                reason = "An unexpected error occurred: ${error.message ?: "Unknown error"}",
                rawData = emptyMap()
            )
        }
        handleResult(failureResult)
    }
}

