package com.dakulangsakalam.customwebview.domain.model

import androidx.annotation.Keep
import java.lang.Exception

sealed class Response<out T> {
    data class Success<out T>(
        val data: T
    ): Response<T>()

    data class Error(
        val exception: Exception
    ): Response<Nothing>()
}
