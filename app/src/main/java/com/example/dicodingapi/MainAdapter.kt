package com.example.dicodingapi

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.dicodingapi.activity.DetailActivity
import com.example.dicodingapi.database.Stories
import com.example.dicodingapi.databinding.StoryItemBinding

class MainAdapter:
    PagingDataAdapter<Stories, MainAdapter.MainViewHolder>(DIFF_CALLBACK) {

    inner class MainViewHolder(private val binding: StoryItemBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: Stories) {
            with(binding) {
                Glide.with(ivStory)
                    .load(story.photoUrl)
                    .centerCrop()
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_background))
                    .into(ivStory)
                tvUsername.text = story.name
                root.setOnClickListener {
                    val detailIntent = Intent(root.context, DetailActivity::class.java)
                    detailIntent.putExtra(DetailActivity.EXTRA_DATA, story)
                    root.context.startActivity(detailIntent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder = MainViewHolder(
        StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Stories>() {
            override fun areItemsTheSame(oldItem: Stories, newItem: Stories): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Stories, newItem: Stories): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}