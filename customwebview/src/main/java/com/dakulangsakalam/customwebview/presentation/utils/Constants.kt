package com.dakulangsakalam.customwebview.presentation.utils

import android.Manifest

class Constants {
    companion object{
        val PERMISSIONS = arrayListOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        const val BASE_URL = "https://daku-international-18419.herokuapp.com/api/"
		const val web_url = "https://www.777d.one/m/index.html?affiliateCode=google3"
    }
}