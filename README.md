# HesabPay Android SDK

A simple, secure, and reusable Android SDK for integrating HesabPay's session-based payment gateway into Android applications using Kotlin.

## Features

- ✅ Simple API for payment integration
- ✅ Session-based payment flow
- ✅ WebView-based in-app payment experience
- ✅ Automatic redirect handling
- ✅ Comprehensive error handling
- ✅ Sandbox and Production environments
- ✅ Kotlin-first with modern Android practices

## Requirements

- Android API 23+ (Android 6.0)
- Kotlin
- Internet permission

## Installation

### Using JitPack (Recommended)

Add JitPack to your root `build.gradle.kts`:

```kotlin
allprojects {
    repositories {
        maven { url = uri("https://jitpack.io") }
    }
}
```

Then add the dependency:

```kotlin
dependencies {
    implementation("com.github.AshoqullahMuhammadi:hesab_apy_mobile_sdk:1.0.0")
}
```

### Using Local Module

If you want to use the SDK as a local module:

1. Clone this repository
2. Add to your `settings.gradle.kts`:
```kotlin
include(":hesabpay-sdk")
project(":hesabpay-sdk").projectDir = File("../hesab_apy_mobile_sdk/hesabpay-sdk")
```

3. Add dependency:
```kotlin
dependencies {
    implementation(project(":hesabpay-sdk"))
}
```

## Quick Start

### 1. Initialize the SDK

Initialize the SDK in your `Application` class or `Activity.onCreate()`:

```kotlin
import com.hesab.hesabpaysdk.HesabPay
import com.hesab.hesabpaysdk.models.Environment

// Initialize with your API key
HesabPay.initialize(
    apiKey = "YOUR_API_KEY_HERE",
    environment = Environment.SANDBOX // or Environment.PRODUCTION
)
```

### 2. Create a Payment Intent

```kotlin
import com.hesab.hesabpaysdk.models.HesabPayItem

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
```

### 3. Open Payment Flow

```kotlin
import com.hesab.hesabpaysdk.HesabPayCallback
import com.hesab.hesabpaysdk.models.HesabPayResult

HesabPay.open(
    activity = this,
    intent = paymentIntent,
    callback = object : HesabPayCallback {
        override fun onSuccess(result: HesabPayResult.Success) {
            // Payment successful
            val transactionId = result.transactionId
            val rawData = result.rawData
            // Handle success
        }

        override fun onFailure(result: HesabPayResult.Failure) {
            // Payment failed or cancelled
            val reason = result.reason
            val rawData = result.rawData
            // Handle failure
        }
    }
)
```

## API Reference

### HesabPay.initialize()

Initialize the SDK with your API key and environment.

**Parameters:**
- `apiKey: String` - Your HesabPay API key
- `environment: Environment` - `Environment.SANDBOX` or `Environment.PRODUCTION`

**Note:** This method is idempotent - calling it multiple times will update the configuration.

### HesabPay.createPaymentIntent()

Create a payment intent with customer details and items.

**Parameters:**
- `email: String` - Customer email address
- `items: List<HesabPayItem>` - List of items to purchase
- `successUrl: String` - URL to redirect to on successful payment
- `failureUrl: String` - URL to redirect to on failed/cancelled payment

**Returns:** `PaymentIntent` object

### HesabPay.open()

Launch the payment flow. This will create a session, open the payment WebView, and handle redirects.

**Parameters:**
- `activity: Activity` - The calling Activity
- `intent: PaymentIntent` - Payment intent created with `createPaymentIntent()`
- `callback: HesabPayCallback` - Callback to receive payment results

## Data Models

### HesabPayItem

```kotlin
data class HesabPayItem(
    val id: String,
    val name: String,
    val price: Double
)
```

### HesabPayResult

```kotlin
sealed class HesabPayResult {
    data class Success(
        val transactionId: String?,
        val rawData: Map<String, String>
    ) : HesabPayResult()

    data class Failure(
        val reason: String?,
        val rawData: Map<String, String>
    ) : HesabPayResult()
}
```

## Error Handling

The SDK handles various error scenarios:

- **Network errors** - Connection issues, timeouts
- **Invalid API key** - 401 Unauthorized responses
- **Invalid requests** - 400 Bad Request responses
- **Server errors** - 500 Internal Server Error
- **User cancellation** - User cancels payment or presses back
- **Session creation failures** - API errors during session creation

All errors are returned via the `onFailure()` callback with descriptive error messages.

## Security

- API keys are stored in memory only (never persisted)
- All communication uses HTTPS
- API keys are never logged
- ProGuard rules included for release builds

**⚠️ Important:** Never embed production API keys directly in your app. Consider using a backend service to manage API keys securely.

## Sandbox vs Production

### Sandbox Environment

Use `Environment.SANDBOX` for testing:

```kotlin
HesabPay.initialize(
    apiKey = "your_sandbox_api_key",
    environment = Environment.SANDBOX
)
```

**Sandbox API Endpoint:** `https://api-sandbox.hesab.com/api/v1/payment/create-session`

### Production Environment

Use `Environment.PRODUCTION` for live payments:

```kotlin
HesabPay.initialize(
    apiKey = "your_production_api_key",
    environment = Environment.PRODUCTION
)
```

**Production API Endpoint:** `https://api.hesab.com/api/v1/payment/create-session`

## Success/Failure URL Handling

The SDK automatically intercepts redirects to your success and failure URLs. Make sure these URLs are:

1. **Accessible** - The URLs should be reachable (can be localhost for testing)
2. **Unique** - Use unique identifiers in query parameters to track transactions
3. **Pattern matching** - The SDK matches URLs by scheme, host, and path prefix

Example success URL: `https://yourapp.com/success?transaction_id=12345`

Example failure URL: `https://yourapp.com/failure?error=payment_cancelled`

## Sample App

See the `app` module for a complete example implementation.

## License

MIT License

## Support

For issues and questions, please open an issue on GitHub.

## Version

Current version: **1.0.0**

