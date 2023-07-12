package com.example.dicodingapi.request

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email")
    val name: String?,

    @SerializedName("password")
    val password: String?
)