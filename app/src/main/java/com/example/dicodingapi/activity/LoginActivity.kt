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
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.dicodingapi.Resource
import com.example.dicodingapi.authentication.AuthViewModel
import com.example.dicodingapi.databinding.ActivityLoginBinding
import com.example.dicodingapi.sharedpref.UserPreferences
import com.example.dicodingapi.model.ViewModelFactory

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_key")
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var authViewModel: AuthViewModel
    private var mShouldFinish = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setupViewModel()
        setupView()
        animationView()
    }

    private fun animationView() {
        val title = ObjectAnimator.ofFloat(binding.imageView, View.ALPHA, 1f).setDuration(4000)
        val animWelcome = ObjectAnimator.ofFloat(binding.tvWelcome, View.ALPHA, 1f).setDuration(3000)
        val animLogin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(3000)
        val animRegister = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(3000)
        animWelcome.start()
        animRegister.start()
        animLogin.start()
        title.start()
    }


    override fun onStop() {
        super.onStop()
        if(mShouldFinish)
            finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClick(p0: View?) {
        when(p0){
            binding.btnRegister -> startActivity(Intent(this, RegisterActivity::class.java))
            binding.btnLogin ->{
                if (canLogin()){
                    val email = binding.etEmail.text.toString()
                    val password = binding.etPassword.text.toString()

                    closeKeyboard(this)
                    authViewModel.login(email, password)
                }
                else{
                    Toast.makeText(this, "Check Your Input", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupViewModel() {
        val pref = UserPreferences.getInstance(dataStore)
        authViewModel = ViewModelProvider(this, ViewModelFactory(pref))[AuthViewModel::class.java]
        authViewModel.authInfo.observe(this){
            when(it){
                is Resource.Success ->{
                    showLoading(false)
                    startActivity(Intent(this, MainActivity::class.java))
                    mShouldFinish = true
                }
                is Resource.Loading -> showLoading(true)
                is Resource.Error -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    showLoading(false)
                }

                else -> {}
            }
        }
    }

    private fun setupView() {
        with(binding){
            btnRegister.setOnClickListener(this@LoginActivity)
            btnLogin.setOnClickListener(this@LoginActivity)
        }
    }

    private fun closeKeyboard(activity: AppCompatActivity){
        val view: View? = activity.currentFocus
        if(view != null){
            val imm: InputMethodManager =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken,0)
        }
    }
    private fun canLogin() =
        binding.etEmail.error == null && binding.etEmail.error == null &&
                !binding.etEmail.text.isNullOrEmpty() && !binding.etEmail.text.isNullOrEmpty()

    private fun showLoading(isLoading: Boolean) {
        binding.isLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !isLoading
    }
}
