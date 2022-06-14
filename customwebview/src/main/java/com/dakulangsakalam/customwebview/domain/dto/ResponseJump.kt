package com.dakulangsakalam.customwebview.domain.dto

import androidx.annotation.Keep

@Keep
data class ResponseJump(
    val httpCode: Int,
    val response: List<Response>
)