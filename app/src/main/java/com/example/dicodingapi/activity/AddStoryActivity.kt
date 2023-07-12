package com.example.dicodingapi.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.dicodingapi.MainAdapter
import com.example.dicodingapi.Resource
import com.example.dicodingapi.createCustomTempFile
import com.example.dicodingapi.databinding.ActivityAddStoryBinding
import com.example.dicodingapi.fixImageRotation
import com.example.dicodingapi.model.AddStoryViewModel
import com.example.dicodingapi.model.ViewModelFactory
import com.example.dicodingapi.reduceFileImage
import com.example.dicodingapi.sharedpref.UserPreferences
import com.example.dicodingapi.uriToFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


class AddStoryActivity : AppCompatActivity(), View.OnClickListener {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_key")
    private var _binding: ActivityAddStoryBinding? = null
    private val binding get() = _binding!!
    private var imageScaleZoom = true
    private var mainAdapter = MainAdapter()
    private lateinit var currentPhotoPath: String
    private lateinit var addStoryViewModel: AddStoryViewModel
    private var getFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSION
            )
        }
        setupViewModel()
        setupView()
    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun setupView() {
        with(binding) {
            btnCamera.setOnClickListener(this@AddStoryActivity)
            btnGalery.setOnClickListener(this@AddStoryActivity)
            btnUpload.setOnClickListener(this@AddStoryActivity)
            btnBackAdd.setOnClickListener(this@AddStoryActivity)
            btnUploadAsGuest.setOnClickListener(this@AddStoryActivity)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupViewModel() {
        val pref = UserPreferences.getInstance(dataStore)
        addStoryViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[AddStoryViewModel::class.java]
        
        addStoryViewModel.upload.observe(this) {
            when (it) {
                is Resource.Success -> {
                    Toast.makeText(this, it.data, Toast.LENGTH_SHORT).show()
                    finish()
                    showLoading(false)
                }
                is Resource.Loading -> {
                    showLoading(true)
                    mainAdapter.refresh()
                }
                is Resource.Error -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    showLoading(false)
                }

                else -> {}
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.isLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun upload(asGuest: Boolean) {
        if (getFile != null){
            val file = reduceFileImage(getFile as File)

            val description = binding.etDeskripsi.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            CoroutineScope(Dispatchers.IO).launch {
                addStoryViewModel.upload(imageMultipart, description, asGuest)
            }

        }
        else{
            Toast.makeText(this, "Please Insert Picture First", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClick(p0: View?) {
        when(p0){
            binding.btnBackAdd -> finish()
            binding.btnCamera -> startCamera()
            binding.btnGalery -> startGalery()
            binding.btnUpload -> upload(asGuest = false)
            binding.btnUploadAsGuest -> upload(asGuest = true)
            binding.ivStoryAdd ->{
                imageScaleZoom = !imageScaleZoom
                binding.ivStoryAdd.scaleType = if (imageScaleZoom) ImageView.ScaleType.CENTER_CROP else ImageView.ScaleType.FIT_CENTER
            }
        }
    }

    private fun startGalery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        inGallery.launch(chooser)
    }

    private val inGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){result->
        if (result.resultCode == RESULT_OK){
            val selImage: Uri = result.data?.data as Uri
            val myFile = uriToFile(selImage, this@AddStoryActivity)
            binding.ivStoryAdd.setImageURI(selImage)
            getFile = myFile
        }
    }

    private val inCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if(it.resultCode == RESULT_OK){
            val myFile = File(currentPhotoPath)
            val options = BitmapFactory.Options()
            val bitmap = BitmapFactory.decodeFile(currentPhotoPath, options)
            myFile.let { file ->
                val fix = fixImageRotation(bitmap, file.path)
                getFile = file
                binding.ivStoryAdd.setImageBitmap(fix)
            }
        }
    }

    private fun startCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddStoryActivity,
                "com.example.dicodingapi.mycamera",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            inCamera.launch(intent)
        }
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSION = 10
    }
}