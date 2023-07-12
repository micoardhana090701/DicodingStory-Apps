package com.example.dicodingapi.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.dicodingapi.MainAdapter
import com.example.dicodingapi.model.MainViewModel
import com.example.dicodingapi.R
import com.example.dicodingapi.authentication.AuthViewModel
import com.example.dicodingapi.databinding.ActivityMainBinding
import com.example.dicodingapi.sharedpref.UserPreferences
import com.example.dicodingapi.model.ViewModelFactory
import com.example.dicodingapi.model.ViewModelStoryFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_key")
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val mainAdapter = MainAdapter()
    private lateinit var mainViewModel: MainViewModel
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Home"

        setupViewModel()
        setupView()

        binding.swRefresh.setOnRefreshListener {
            refresh()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        mainAdapter.refresh()
        binding.rvStory.smoothScrollToPosition(0)
        fetchData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.mainmenu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.btnLogout -> {
                authViewModel.logout()
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finishAffinity()
                true
            }
            R.id.btnMaps -> {
                startActivity(Intent(this@MainActivity, MapsActivity::class.java))
                true
            }
            else -> false
        }
    }

    private fun refresh() {
        var swipeRefreshLayout = binding.swRefresh
        swipeRefreshLayout.isRefreshing = false
        mainAdapter.refresh()
    }


    private fun setupViewModel() {
        val pref = UserPreferences.getInstance(dataStore)
        val viewModelFactory = ViewModelFactory(pref)
        viewModelFactory.setApplication(application)

        authViewModel = ViewModelProvider(this, ViewModelFactory(pref))[AuthViewModel::class.java]
        mainViewModel = ViewModelProvider(this, ViewModelStoryFactory(this))[MainViewModel::class.java]


        mainViewModel.getStories().observe(this) {
            mainAdapter.submitData(lifecycle, it)
        }
        fetchData()
    }

    private fun fetchData() {
        CoroutineScope(Dispatchers.IO).launch {
            mainViewModel.getStories()
        }
    }

    private fun setupView() {
        setupRecylerView()
        fab()
    }

    private fun fab() {
        binding.fabFav.setOnClickListener{
            val addIntent = Intent(this, AddStoryActivity::class.java)
            startActivity(addIntent)
        }
    }

    private fun setupRecylerView() {
        with(binding.rvStory) {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(this@MainActivity, RV_COLOMN_COUNT)
            adapter = mainAdapter
        }
    }

    companion object {
        const val RV_COLOMN_COUNT = 1
    }
}