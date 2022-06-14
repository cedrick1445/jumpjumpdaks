package com.dakulangsakalam.customwebview.domain.dto

import androidx.annotation.Keep

@Keep
data class Response(
    val androidname: String,
    val category: Int,
    val categoryname: String,
    val city: String,
    val country: String,
    val createtime: String,
    val drainage: String,
    val id: String,
    val installnumber: Int,
    val isNewRecord: Boolean,
    val off: String,
    val pingtai: String,
    val region: String,
    val shieldCitys: String,
    val versionNumber: Int,
    val wangzhi: String,
    val yingyongming: String
)