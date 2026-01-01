# Changelog

All notable changes to the HesabPay Android SDK will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2024-01-XX

### Added
- Initial release of HesabPay Android SDK
- Session-based payment flow integration
- WebView Activity for in-app payment experience
- Support for Sandbox and Production environments
- Comprehensive error handling
- Success and failure URL redirect handling
- Payment intent creation API
- Callback-based result handling
- Sample app demonstrating SDK usage
- ProGuard rules for release builds
- Complete documentation and README

### Features
- Initialize SDK with API key and environment
- Create payment intents with items and customer email
- Open payment flow with WebView Activity
- Automatic redirect detection and result parsing
- Transaction ID extraction from success URLs
- Error messages for common failure scenarios
- Back button handling for user cancellation

### Security
- API keys stored in memory only
- HTTPS-only communication
- No API key logging
- ProGuard obfuscation support

### Technical Details
- Minimum SDK: Android 23 (Android 6.0)
- Language: Kotlin
- Networking: Retrofit + OkHttp
- JSON Serialization: Kotlinx Serialization
- UI: WebView-based Activity

