package com.example.dicodingapi.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.dicodingapi.StoriesRepository
import com.example.dicodingapi.database.Stories

class MainViewModel(private val repository: StoriesRepository): ViewModel() {

    fun getStories(): LiveData<PagingData<Stories>> =
        repository.getStories().cachedIn(viewModelScope)
}