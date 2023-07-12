package com.example.dicodingapi.model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dicodingapi.authentication.AuthViewModel
import com.example.dicodingapi.sharedpref.UserPreferences

class ViewModelFactory(private val pref: UserPreferences): ViewModelProvider.NewInstanceFactory() {
    private lateinit var _Application: Application

    fun setApplication(application: Application){
        _Application = application
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            AuthViewModel::class.java -> AuthViewModel(pref) as T
            AddStoryViewModel::class.java -> AddStoryViewModel(pref) as T
            MapsViewModel::class.java -> MapsViewModel(pref, _Application) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}