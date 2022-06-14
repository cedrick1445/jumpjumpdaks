package com.dakulangsakalam.customwebview.data.repository

import com.dakulangsakalam.customwebview.domain.dto.ResponseJump
import com.dakulangsakalam.customwebview.domain.model.JumpRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface JumpRepo {

    @POST("details")
    suspend fun startRequest(@Body param: JumpRequest): Response<ResponseJump>

}