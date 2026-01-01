package com.hesab.hesabpaysdk.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class HesabPayResult : Parcelable {
    @Parcelize
    data class Success(
        val transactionId: String?,
        val rawData: Map<String, String>
    ) : HesabPayResult()

    @Parcelize
    data class Failure(
        val reason: String?,
        val rawData: Map<String, String>
    ) : HesabPayResult()
}

