package com.example.dicodingapi

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import com.example.dicodingapi.api.ApiConfig
import com.example.dicodingapi.sharedpref.UserPreferences
import androidx.datastore.preferences.core.Preferences
import com.example.dicodingapi.database.StoriesDatabase

object Injection {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_key")
    fun provideRepository(context: Context): StoriesRepository{
        val database = StoriesDatabase.getDatabase(context)
        val apiService = ApiConfig.apiInstance
        val pref = UserPreferences.getInstance(context.dataStore)
        return StoriesRepository(database, apiService, pref)
    }
}