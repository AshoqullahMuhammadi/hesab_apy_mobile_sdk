package com.hesab.hesabpaysdk.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.hesab.hesabpaysdk.HesabPay
import com.hesab.hesabpaysdk.models.HesabPayResult

class HesabPayWebViewActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PAYMENT_URL = "extra_payment_url"
        const val EXTRA_SUCCESS_URL = "extra_success_url"
        const val EXTRA_FAILURE_URL = "extra_failure_url"
    }

    private lateinit var webView: WebView
    private var resultHandled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val paymentUrl = intent.getStringExtra(EXTRA_PAYMENT_URL)
        val successUrl = intent.getStringExtra(EXTRA_SUCCESS_URL)
        val failureUrl = intent.getStringExtra(EXTRA_FAILURE_URL)

        if (paymentUrl == null || successUrl == null || failureUrl == null) {
            finish()
            return
        }

        setupWebView()

        webView.webViewClient = PaymentWebViewClient(
            successUrl = successUrl,
            failureUrl = failureUrl,
            onResult = { result ->
                if (!resultHandled) {
                    resultHandled = true
                    navigateToResultScreen(result)
                }
            },
            onFinish = {
                if (!resultHandled) {
                    resultHandled = true
                    // User cancelled or closed
                    navigateToResultScreen(
                        HesabPayResult.Failure(
                            reason = "Payment was cancelled",
                            rawData = emptyMap()
                        )
                    )
                }
            }
        )

        webView.loadUrl(paymentUrl)
    }

    private fun navigateToResultScreen(result: HesabPayResult) {
        val intent = Intent(this, HesabPayResultComposeActivity::class.java).apply {
            putExtra(HesabPayResultComposeActivity.EXTRA_RESULT, result)
        }
        startActivity(intent)
        finish()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        // Create container with padding
        val container = FrameLayout(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setPadding(16.dp, 32.dp, 16.dp, 32.dp)
        }

        // Handle system insets for proper padding
        ViewCompat.setOnApplyWindowInsetsListener(container) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                top = systemBars.top + 32.dp,
                bottom = systemBars.bottom + 32.dp,
                left = 16.dp,
                right = 16.dp
            )
            insets
        }

        webView = WebView(this).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.builtInZoomControls = false
            settings.displayZoomControls = false
            settings.setSupportZoom(false)
            
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        container.addView(webView)
        setContentView(container)
    }

    private val Int.dp: Int
        get() = (this * resources.displayMetrics.density).toInt()

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (::webView.isInitialized && webView.canGoBack()) {
            webView.goBack()
        } else {
            if (!resultHandled) {
                resultHandled = true
                navigateToResultScreen(
                    HesabPayResult.Failure(
                        reason = "Payment was cancelled by user",
                        rawData = emptyMap()
                    )
                )
            } else {
                super.onBackPressed()
            }
        }
    }

    override fun onDestroy() {
        if (!resultHandled) {
            // Activity is being destroyed without a result (e.g., system killed it)
            navigateToResultScreen(
                HesabPayResult.Failure(
                    reason = "Payment flow was interrupted",
                    rawData = emptyMap()
                )
            )
            // Clear stored URLs
            com.hesab.hesabpaysdk.utils.PaymentUrlStore.clearUrls(this)
        }
        super.onDestroy()
    }
}

