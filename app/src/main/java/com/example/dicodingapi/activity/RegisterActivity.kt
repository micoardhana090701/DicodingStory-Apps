package com.example.dicodingapi.activity

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.dicodingapi.Resource
import com.example.dicodingapi.authentication.AuthViewModel
import com.example.dicodingapi.databinding.ActivityRegisterBinding
import com.example.dicodingapi.sharedpref.UserPreferences
import com.example.dicodingapi.model.ViewModelFactory

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    private val Context.dataStore: DataStore<androidx.datastore.preferences.core.Preferences> by preferencesDataStore(name = "user_key")
    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setupViewModel()
        setupView()
        playAnimation()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupViewModel() {
        val pref = UserPreferences.getInstance(dataStore)
        authViewModel = ViewModelProvider(this, ViewModelFactory(pref))[AuthViewModel::class.java]

        authViewModel.authInfo.observe(this) {
            when (it) {
                is Resource.Success -> {
                    showLoading(false)
                    Toast.makeText(this, it.data, Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finishAffinity()
                }
                is Resource.Loading -> showLoading(true)
                is Resource.Error -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    showLoading(false)
                }
            }
        }
    }

    private fun setupView() {
        with(binding) {
            btnRegisterNow.setOnClickListener(this@RegisterActivity)
            btnBack.setOnClickListener(this@RegisterActivity)
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.btnBack -> finish()
            binding.btnRegisterNow -> {
                val name = binding.etUsernameR.text.toString()
                val email = binding.etEmailR.text.toString()
                val password = binding.etPasswordR.text.toString()

                if (binding.etEmailR.error == null && binding.etPasswordR.error == null) {
                    closeKeyboard(this)
                    authViewModel.register(name, email, password)
                } else {
                    Toast.makeText(this, "Check Your Input", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun playAnimation() {
        val title = ObjectAnimator.ofFloat(binding.ivLogoRegister, View.ALPHA, 1f).setDuration(3000)
        val animWelcome = ObjectAnimator.ofFloat(binding.tvSelamat, View.ALPHA, 1f).setDuration(3000)
        val animRegister = ObjectAnimator.ofFloat(binding.btnRegisterNow, View.ALPHA, 1f).setDuration(3000)

        title.start()
        animWelcome.start()
        animRegister.start()
    }

    private fun closeKeyboard(activity: AppCompatActivity) {
        val view: View? = activity.currentFocus
        if (view != null) {
            val imm: InputMethodManager =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.registerLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnRegisterNow.isEnabled = !isLoading
    }
}