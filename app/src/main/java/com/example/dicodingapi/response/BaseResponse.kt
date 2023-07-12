package com.example.dicodingapi.response

import com.google.gson.annotations.SerializedName

data class BaseResponse(
    @field:SerializedName("error")
    val error: Boolean?,

    @field:SerializedName("message")
    val message: String?
)