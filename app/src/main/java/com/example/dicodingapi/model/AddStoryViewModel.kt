package com.example.dicodingapi.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dicodingapi.Resource
import com.example.dicodingapi.api.ApiConfig
import com.example.dicodingapi.response.BaseResponse
import com.example.dicodingapi.sharedpref.UserPreferences
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddStoryViewModel(private val preferences: UserPreferences): ViewModel() {
    private val _upload = MutableLiveData<Resource<String>>()
    val upload : LiveData<Resource<String>> = _upload

    suspend fun upload(
        imageMultipart: MultipartBody.Part,
        descrition: RequestBody,
        asGuest: Boolean = false,
    ){
        _upload.postValue(Resource.Loading())
        val client = if(asGuest) ApiConfig.apiInstance.addGuestStories(
            imageMultipart,
            descrition,
        ) else ApiConfig.apiInstance.addStories(
            token = "Bearer ${preferences.getUserKey().first()}",
            imageMultipart,
            descrition,
        )
        client.enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.isSuccessful) {
                    _upload.postValue(Resource.Success(response.body()?.message))
                } else {
                    val errorResponse = Gson().fromJson(
                        response.errorBody()?.charStream(),
                        BaseResponse::class.java
                    )
                    _upload.postValue(Resource.Error(errorResponse.message))
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                _upload.postValue(Resource.Error(t.message))
            }
        })
    }
}