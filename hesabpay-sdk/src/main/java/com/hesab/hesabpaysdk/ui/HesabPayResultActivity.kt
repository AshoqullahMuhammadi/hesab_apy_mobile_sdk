package com.hesab.hesabpaysdk.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hesab.hesabpaysdk.HesabPay
import com.hesab.hesabpaysdk.models.HesabPayResult
import com.hesab.hesabpaysdk.utils.PaymentUrlStore

/**
 * Activity that handles deep link redirects from payment success/failure URLs.
 * This activity is launched when the payment gateway redirects to the success/failure URLs.
 */
class HesabPayResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Handle the deep link intent
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val data: Uri? = intent?.data
        if (data == null) {
            finish()
            return
        }

        val url = data.toString()
        
        // Get stored success/failure URLs
        val (successUrl, failureUrl) = PaymentUrlStore.getUrls(this)
        
        val result = if (successUrl != null && failureUrl != null) {
            // Use stored URLs for proper matching
            RedirectHandler.parseResult(url, successUrl, failureUrl)
        } else {
            // Fallback to pattern matching
            parseDeepLinkResult(url)
        }
        
        val finalResult = result ?: HesabPayResult.Failure(
            reason = "Unable to parse payment result",
            rawData = mapOf("url" to url)
        )
        
        // Navigate to result screen
        val intent = android.content.Intent(this, HesabPayResultComposeActivity::class.java).apply {
            putExtra(HesabPayResultComposeActivity.EXTRA_RESULT, finalResult)
        }
        startActivity(intent)
        
        // Clear stored URLs after handling
        PaymentUrlStore.clearUrls(this)
        finish()
    }

    private fun parseDeepLinkResult(url: String): HesabPayResult? {
        // Fallback parser when stored URLs are not available
        val uri = Uri.parse(url)
        val queryParams = mutableMapOf<String, String>()
        
        uri.queryParameterNames.forEach { key ->
            uri.getQueryParameter(key)?.let { value ->
                queryParams[key] = value
            }
        }
        
        // Check if URL contains success indicators
        val urlLower = url.lowercase()
        return when {
            urlLower.contains("success") || 
            queryParams.containsKey("transaction_id") ||
            queryParams.containsKey("txn_id") ||
            queryParams.containsKey("id") -> {
                HesabPayResult.Success(
                    transactionId = queryParams["transaction_id"] 
                        ?: queryParams["txn_id"] 
                        ?: queryParams["id"]
                        ?: queryParams["transactionId"],
                    rawData = queryParams
                )
            }
            urlLower.contains("failure") || 
            urlLower.contains("error") || 
            urlLower.contains("cancel") -> {
                HesabPayResult.Failure(
                    reason = queryParams["error"] 
                        ?: queryParams["reason"] 
                        ?: queryParams["message"]
                        ?: "Payment failed",
                    rawData = queryParams
                )
            }
            else -> null
        }
    }
}

