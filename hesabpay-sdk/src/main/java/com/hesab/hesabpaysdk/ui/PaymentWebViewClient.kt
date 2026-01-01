package com.hesab.hesabpaysdk.ui

import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.hesab.hesabpaysdk.models.HesabPayResult

class PaymentWebViewClient(
    private val successUrl: String,
    private val failureUrl: String,
    private val onResult: (HesabPayResult) -> Unit,
    private val onFinish: () -> Unit
) : WebViewClient() {

    private var resultHandled = false

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        val url = request?.url?.toString() ?: return false

        // Check if this is a success or failure redirect
        val result = RedirectHandler.parseResult(url, successUrl, failureUrl)
        
        if (result != null && !resultHandled) {
            resultHandled = true
            onResult(result)
            onFinish()
            return true
        }

        // Allow normal navigation for other URLs
        return false
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        
        // Double-check URL in case shouldOverrideUrlLoading didn't catch it
        if (url != null && !resultHandled) {
            val result = RedirectHandler.parseResult(url, successUrl, failureUrl)
            if (result != null) {
                resultHandled = true
                onResult(result)
                onFinish()
            }
        }
    }
}

