package com.example.dicodingapi

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityOptionsCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.dicodingapi.activity.LoginActivity
import com.example.dicodingapi.activity.MainActivity
import com.example.dicodingapi.authentication.AuthViewModel
import com.example.dicodingapi.databinding.SplashScreenBinding
import com.example.dicodingapi.sharedpref.UserPreferences
import com.example.dicodingapi.model.ViewModelFactory

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {

    private val Context.dataStore: DataStore<androidx.datastore.preferences.core.Preferences> by preferencesDataStore(name = "user_key")
    private var _binding: SplashScreenBinding? = null
    private val binding get() = _binding!!
    private var mShouldFinish = false
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = SplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setupViewModel()

        Handler(Looper.getMainLooper()).postDelayed({
            val optionsCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this,
                    binding.tvSplash,
                    "logoLogin"
                )

            authViewModel.getUserKey().observe(this) {
                if (it.isNullOrEmpty()) {
                    startActivity(Intent(this@SplashScreen, LoginActivity::class.java), optionsCompat.toBundle())
                } else {
                    startActivity(Intent(this@SplashScreen, MainActivity::class.java), optionsCompat.toBundle())
                }
            }

            mShouldFinish = true
        }, DELAY)
    }

    override fun onStop() {
        super.onStop()
        if (mShouldFinish) {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupViewModel() {
        val pref = UserPreferences.getInstance(dataStore)
        authViewModel = ViewModelProvider(this, ViewModelFactory(pref))[AuthViewModel::class.java]
    }

    companion object {
        const val DELAY = 5000L
    }
}