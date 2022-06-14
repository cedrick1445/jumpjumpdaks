package com.dakulangsakalam.customwebview.presentation.utils

import android.util.Log

fun writeLogs(message: String){
    Log.d("DAKULANGSAKALAMS", message)
}

fun writeLogs(e: Exception){
    Log.d("DAKULANGSAKALAMS", """
        Error: ${e.cause?.message}
        Description: ${e.message}
    """.trimIndent())
}