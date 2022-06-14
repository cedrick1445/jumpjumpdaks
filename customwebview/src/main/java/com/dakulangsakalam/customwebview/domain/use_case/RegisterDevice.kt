package com.dakulangsakalam.customwebview.domain.use_case

import com.dakulangsakalam.customwebview.data.repository.JumpRepoImp
import com.dakulangsakalam.customwebview.domain.model.JumpRequest
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class RegisterDevice {
    private val repo = JumpRepoImp()

    suspend operator fun invoke(param: JumpRequest) = repo.startRequest(param)
}