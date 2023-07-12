package com.example.dicodingapi.response

import com.example.dicodingapi.Login
import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @field:SerializedName("loginResult")
    val loginResult: Login?,

    @field:SerializedName("error")
    val error: Boolean?,

    @field:SerializedName("message")
    val message: String?
)