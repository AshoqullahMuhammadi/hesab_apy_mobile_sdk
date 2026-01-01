package com.hesab.hesabpaysdk.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.hesab.hesabpaysdk.models.HesabPayResult
import com.hesab.hesabpaysdk.ui.compose.PaymentFailureScreen
import com.hesab.hesabpaysdk.ui.compose.PaymentSuccessScreen

class HesabPayResultComposeActivity : ComponentActivity() {

    companion object {
        const val EXTRA_RESULT = "extra_result"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val result = intent.getParcelableExtra<HesabPayResult>(EXTRA_RESULT)
            ?: run {
                finish()
                return
            }

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (result) {
                        is HesabPayResult.Success -> {
                            PaymentSuccessScreen(
                                result = result,
                                onClose = {
                                    com.hesab.hesabpaysdk.HesabPay.handleResult(result)
                                    finish()
                                }
                            )
                        }
                        is HesabPayResult.Failure -> {
                            PaymentFailureScreen(
                                result = result,
                                onClose = {
                                    com.hesab.hesabpaysdk.HesabPay.handleResult(result)
                                    finish()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

