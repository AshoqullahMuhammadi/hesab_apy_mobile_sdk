package com.hesab.hesabpaysdk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hesab.hesabpaysdk.models.Environment
import com.hesab.hesabpaysdk.models.HesabPayItem
import com.hesab.hesabpaysdk.models.HesabPayResult
import com.hesab.hesabpaysdk.ui.theme.HesabPaySdKTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize HesabPay SDK
        // TODO: Replace with your actual API key
        HesabPay.initialize(
            apiKey = "ZGE2MGNmNWQtNDU0YS00YTRhLTkwNjUtYjcyN2FhMmU4N2IwX180ZjUzZTI0ZGVlZGEwNzllOWI5ZQ==",
            environment = Environment.SANDBOX
        )
        
        enableEdgeToEdge()
        setContent {
            HesabPaySdKTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PaymentDemoScreen(
                        activity = this,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun PaymentDemoScreen(
    activity: ComponentActivity,
    modifier: Modifier = Modifier
) {
    var resultMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "HesabPay SDK Demo",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = {
                isLoading = true
                resultMessage = null
                
                // Create payment intent
                val paymentIntent = HesabPay.createPaymentIntent(
                    email = "customer@example.com",
                    items = listOf(
                        HesabPayItem(
                            id = "item1",
                            name = "Product 1",
                            price = 45.0
                        ),
                        HesabPayItem(
                            id = "item2",
                            name = "Product 2",
                            price = 20.0
                        )
                    ),
                    successUrl = "https://yourapp.com/success",
                    failureUrl = "https://yourapp.com/failure"
                )
                
                // Open payment flow
                HesabPay.open(
                    activity = activity,
                    intent = paymentIntent,
                    callback = object : HesabPayCallback {
                        override fun onSuccess(result: HesabPayResult.Success) {
                            isLoading = false
                            resultMessage = "Payment successful!\nTransaction ID: ${result.transactionId ?: "N/A"}"
                        }

                        override fun onFailure(result: HesabPayResult.Failure) {
                            isLoading = false
                            resultMessage = "Payment failed: ${result.reason ?: "Unknown error"}"
                        }
                    }
                )
            },
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text("Start Payment")
        }
        
        resultMessage?.let { message ->
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = message,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}