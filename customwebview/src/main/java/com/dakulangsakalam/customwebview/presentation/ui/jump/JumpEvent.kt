package com.dakulangsakalam.customwebview.presentation.ui.jump

import com.dakulangsakalam.customwebview.domain.dto.Response

sealed class JumpEvent {
    data class Loading(val isLoading: Boolean): JumpEvent()
    data class AppInstalledEvent(val isInstalled: Boolean): JumpEvent()
    data class JumpRequestSuccess(val list: Response): JumpEvent()
    data class JumpRequestError(val exception: Exception, val requestType: RequestType): JumpEvent()
}

enum class RequestType(val type: String){
    INSTALL("install"),
    ANDROID_API("androidAPI")
}
