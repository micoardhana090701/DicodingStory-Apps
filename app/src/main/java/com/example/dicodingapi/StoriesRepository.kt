package com.example.dicodingapi

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.example.dicodingapi.api.ApiService
import com.example.dicodingapi.database.Stories
import com.example.dicodingapi.database.StoriesDatabase
import com.example.dicodingapi.sharedpref.UserPreferences

class StoriesRepository(
    private val storiesDatabase: StoriesDatabase,
    private val apiService: ApiService,
    private val token: UserPreferences
)
{

    fun getStories(): LiveData<PagingData<Stories>> {

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoriesRemoteMediator(storiesDatabase, apiService, token),
            pagingSourceFactory = {
                storiesDatabase.storiesDao().findAll()
            }
        ).liveData
    }
}