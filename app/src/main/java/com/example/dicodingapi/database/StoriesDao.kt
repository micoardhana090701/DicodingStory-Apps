package com.example.dicodingapi.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StoriesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(Stories: Stories)

    @Query("DELETE FROM stories")
    suspend fun deleteAll(): Int

    @Query("SELECT * FROM stories")
    fun findAll(): PagingSource<Int, Stories>
}