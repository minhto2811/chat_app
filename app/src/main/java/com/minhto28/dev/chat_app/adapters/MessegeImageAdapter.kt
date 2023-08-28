package com.minhto28.dev.chat_app.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.minhto28.dev.chat_app.databinding.MessageImageLayoutBinding

class MessegeImageAdapter(private val list: List<String>) :
    RecyclerView.Adapter<MessegeImageAdapter.ImageViewHolder>() {


    class ImageViewHolder(val binding: MessageImageLayoutBinding, val context: Context) :
        ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding =
            MessageImageLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding, parent.context)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                Glide.with(context).load(this).into(binding.imvItemImage)
            }
        }
    }
}