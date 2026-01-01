package com.hesab.hesabpaysdk.utils

import android.content.Context
import android.content.SharedPreferences

internal object PaymentUrlStore {
    private const val PREFS_NAME = "hesabpay_sdk_prefs"
    private const val KEY_SUCCESS_URL = "success_url"
    private const val KEY_FAILURE_URL = "failure_url"

    fun storeUrls(context: Context, successUrl: String, failureUrl: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_SUCCESS_URL, successUrl)
            .putString(KEY_FAILURE_URL, failureUrl)
            .apply()
    }

    fun getUrls(context: Context): Pair<String?, String?> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return Pair(
            prefs.getString(KEY_SUCCESS_URL, null),
            prefs.getString(KEY_FAILURE_URL, null)
        )
    }

    fun clearUrls(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_SUCCESS_URL)
            .remove(KEY_FAILURE_URL)
            .apply()
    }
}

