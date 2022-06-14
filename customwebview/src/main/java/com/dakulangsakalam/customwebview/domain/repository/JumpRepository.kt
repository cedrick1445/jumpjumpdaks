package com.dakulangsakalam.customwebview.domain.repository

import com.dakulangsakalam.customwebview.domain.dto.ResponseJump
import com.dakulangsakalam.customwebview.domain.model.JumpRequest
import retrofit2.Response

interface JumpRepository {
    suspend fun startRequest(param: JumpRequest): Response<ResponseJump>
}