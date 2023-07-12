package com.example.dicodingapi.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.dicodingapi.R
import com.example.dicodingapi.database.Stories
import com.example.dicodingapi.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity(), View.OnClickListener {

    private var _binding: ActivityDetailBinding? = null
    private val binding get() = _binding!!
    private var imageScaleZoom = true



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val story = intent.getParcelableExtra<Stories>(EXTRA_DATA)
        setupView(story)
    }

    private fun setupView(story: Stories?) {
        with(binding){
            Glide.with(ivStoryDetail.context)
                .load(story?.photoUrl)
                .centerCrop()
                .apply(RequestOptions.placeholderOf(R.drawable.image))
                .into(ivStoryDetail)
            tvUsernameDetail.text= story?.name
            tvDescriptionDetail.text = story?.description
            ivStoryDetail.setOnClickListener(this@DetailActivity)
            btnBackDetail.setOnClickListener(this@DetailActivity)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClick(v: View?) {
        when(v){
            binding.btnBackDetail -> finish()
            binding.ivStoryDetail -> {
                imageScaleZoom = !imageScaleZoom
                binding.ivStoryDetail.scaleType =
                    if (imageScaleZoom) ImageView.ScaleType.CENTER_CROP else ImageView.ScaleType.FIT_CENTER
            }
        }
    }

    companion object {
        const val EXTRA_DATA = "extra_data"
    }
}