package com.dakulangsakalam.customwebview.presentation.helper

import android.content.Context
import android.content.ContextWrapper
import com.dakulangsakalam.customwebview.presentation.utils.writeLogs

class SharedPrefHelper(context: Context): ContextWrapper(context) {

    val sharedPreferences by lazy {
        getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
    }

    companion object{
        private const val SHARED_PREFS = "sharedPrefs"
        const val APP_FRESH_INSTALLED = "APP_FRESH_INSTALLED"
    }
}