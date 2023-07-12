package com.example.dicodingapi.model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dicodingapi.Resource
import com.example.dicodingapi.api.ApiConfig
import com.example.dicodingapi.database.Stories
import com.example.dicodingapi.database.StoriesDao
import com.example.dicodingapi.database.StoriesDatabase
import com.example.dicodingapi.response.BaseResponse
import com.example.dicodingapi.response.StoriesResponse
import com.example.dicodingapi.sharedpref.UserPreferences
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import retrofit2.Call
import retrofit2.Response
import java.util.prefs.Preferences
import javax.security.auth.callback.Callback

class MapsViewModel (private val preferences: UserPreferences, application: Application): ViewModel(){
    private var storiesDao : StoriesDao? = null
    private var storiesDatabase : StoriesDatabase? = StoriesDatabase.getDatabase(application)

    private val _stories = MutableLiveData<Resource<ArrayList<Stories>>>()
    val stories: LiveData<Resource<ArrayList<Stories>>> = _stories

    init {
        storiesDao = storiesDatabase?.storiesDao()
    }

    suspend fun getStories(){
        _stories.postValue(Resource.Loading())
        val client = ApiConfig.apiInstance.getLocation(token = "Bearer ${preferences.getUserKey().first()}")

        client.enqueue(object : retrofit2.Callback<StoriesResponse>{
            override fun onResponse(
                call: Call<StoriesResponse>,
                response: Response<StoriesResponse>
            ){
                if (response.isSuccessful){
                    response.body()?.let {
                        val listStories = it.listStory
                        _stories.postValue(Resource.Success(ArrayList(listStories)))
                    }
                }
                else{
                    val errorResponse = Gson().fromJson(
                        response.errorBody()?.charStream(),
                        BaseResponse::class.java
                    )
                    _stories.postValue(Resource.Error(errorResponse.message))
                }
            }
            override fun onFailure(call: Call<StoriesResponse>, t: Throwable){
                _stories.postValue(Resource.Error(t.message))
            }
        })

    }
}
