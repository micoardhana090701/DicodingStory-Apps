package com.example.dicodingapi.api

import com.example.dicodingapi.request.LoginRequest
import com.example.dicodingapi.request.RegisterRequest
import com.example.dicodingapi.response.BaseResponse
import com.example.dicodingapi.response.LoginResponse
import com.example.dicodingapi.response.StoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("register")
    fun register(@Body request: RegisterRequest): Call<BaseResponse>

    @POST("login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
    ): StoriesResponse


    @Multipart
    @POST("stories")
    fun addStories(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<BaseResponse>

    @Multipart
    @POST("stories/guest")
    fun addGuestStories(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<BaseResponse>

    @GET("stories?location=1")
    fun getLocation(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
    ): Call<StoriesResponse>
}