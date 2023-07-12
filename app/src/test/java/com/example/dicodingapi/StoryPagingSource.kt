package com.example.dicodingapi

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.dicodingapi.database.Stories

class StoryPagingSource: PagingSource<Int, LiveData<List<Stories>>>() {

    companion object{
        fun snapshot(item: List<Stories>): PagingData<Stories>{
            return PagingData.from(item)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<Stories>>>): Int? {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<Stories>>> {
        return LoadResult.Page(emptyList(), 0,1)
    }

}