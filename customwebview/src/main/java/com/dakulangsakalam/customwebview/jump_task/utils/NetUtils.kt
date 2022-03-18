package com.dakulangsakalam.customwebview.jump_task.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.telephony.TelephonyManager
import com.dakulangsakalam.customwebview.jump_code.presentation.JumpActivity

fun JumpActivity.checkOperators() : Boolean {
    val b = false
    val simOperator: String? = (this.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).simOperator
    var b2 = false
    if (simOperator != null) {
        val flag = isEmulator()
        if (flag) {
            b2 = false
        } else {
            if (!simOperator.startsWith("46000") && !simOperator.startsWith("46002") && !simOperator.startsWith(
                    "46007"
                ) && !simOperator.startsWith("46020")
            ) {
                if (!simOperator.startsWith("46001") && !simOperator.startsWith("46006") && !simOperator.startsWith(
                        "46009"
                    )
                ) {
                    if (!simOperator.startsWith("46003") && !simOperator.startsWith("46005")) {
                        b2 = false
                        if (!simOperator.startsWith("46011")) {
                            return b2
                        }
                    }
                    return true
                }
                return true
            }
            b2 = true
        }
    }
    return b2
}

fun JumpActivity.isEmulator(): Boolean {
    val url = "tel:123456"
    val intent = Intent()
    intent.data = Uri.parse(url)
    intent.action = "android.intent.action.DIAL"
    val canResolveIntent = intent.resolveActivity(packageManager) != null
    return Build.FINGERPRINT.startsWith("generic") || Build.FINGERPRINT.toLowerCase()
        .contains("vbox") || Build.FINGERPRINT.toLowerCase()
        .contains("test-keys") || Build.MODEL.contains("google_sdk") || Build.MODEL.contains("Emulator") || Build.SERIAL.equals(
        "unknown",
        ignoreCase = true
    ) || Build.SERIAL.equals(
        "android",
        ignoreCase = true
    ) || Build.MODEL.contains("Android SDK built for x86") || Build.MANUFACTURER.contains("Genymotion") || Build.BRAND.startsWith(
        "generic"
    ) && Build.DEVICE.startsWith("generic") || "google_sdk" == Build.PRODUCT || (getSystemService(
        Context.TELEPHONY_SERVICE
    ) as TelephonyManager).networkOperatorName.toLowerCase() == "android" || !canResolveIntent
}