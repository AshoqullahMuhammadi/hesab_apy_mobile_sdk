package com.hesab.hesabpaysdk.ui

import android.net.Uri
import com.hesab.hesabpaysdk.models.HesabPayResult

object RedirectHandler {
    fun parseResult(url: String, successUrl: String, failureUrl: String): HesabPayResult? {
        val uri = Uri.parse(url)
        val successUri = Uri.parse(successUrl)
        val failureUri = Uri.parse(failureUrl)

        return when {
            matchesUrl(uri, successUri) -> {
                val queryParams = extractQueryParams(uri)
                HesabPayResult.Success(
                    transactionId = queryParams["transaction_id"] 
                        ?: queryParams["txn_id"] 
                        ?: queryParams["id"]
                        ?: queryParams["transactionId"],
                    rawData = queryParams
                )
            }
            matchesUrl(uri, failureUri) -> {
                val queryParams = extractQueryParams(uri)
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

    private fun matchesUrl(current: Uri, target: Uri): Boolean {
        // Check if current URL matches target (exact or prefix match)
        val currentScheme = current.scheme?.lowercase()
        val currentHost = current.host?.lowercase()
        val currentPath = current.path ?: ""

        val targetScheme = target.scheme?.lowercase()
        val targetHost = target.host?.lowercase()
        val targetPath = target.path ?: ""

        return currentScheme == targetScheme &&
                currentHost == targetHost &&
                (currentPath == targetPath || currentPath.startsWith(targetPath))
    }

    private fun extractQueryParams(uri: Uri): Map<String, String> {
        val params = mutableMapOf<String, String>()
        uri.queryParameterNames.forEach { key ->
            uri.getQueryParameter(key)?.let { value ->
                params[key] = value
            }
        }
        return params
    }
}

