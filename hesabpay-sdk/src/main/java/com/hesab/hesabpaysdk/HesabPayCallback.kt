package com.hesab.hesabpaysdk

import com.hesab.hesabpaysdk.models.HesabPayResult

interface HesabPayCallback {
    fun onSuccess(result: HesabPayResult.Success)
    fun onFailure(result: HesabPayResult.Failure)
}

