package com.dakulangsakalam.customwebview.jump_code.presentation

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.dakulangsakalam.customwebview.jump_code.presentation.helper.SharedPrefHelper
import com.dakulangsakalam.customwebview.jump_code.presentation.utils.writeLogs

fun Context.isNetworkConnected() : Boolean {
    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                        return true
                    }
                }
            }
        } else {
            val mNetworkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            return mNetworkInfo?.isAvailable ?: false
        }
    return false
}

fun Context.showToast(message: String){
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun JumpActivity.getAppIsRegistered(): Boolean {
    writeLogs("haveInstallAddOneTimes ${getDefaultSharedPref().getBoolean("haveInstallAddOneTimes",false)}")
    return getDefaultSharedPref().getBoolean("haveInstallAddOneTimes",false)
}

fun JumpActivity.getDefaultSharedPref(): SharedPreferences{
    return SharedPrefHelper.getInstance(this).sharedPreferences
}


