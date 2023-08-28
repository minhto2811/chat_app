package com.minhto28.dev.chat_app.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.minhto28.dev.chat_app.databinding.ImageLayoutBinding

class ImageAdapter : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    var list = ArrayList<Uri>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class ImageViewHolder(val binding: ImageLayoutBinding, val context: Context) :
        ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ImageLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding, parent.context)
    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                val inputStream = context.contentResolver.openInputStream(this)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                binding.imvImage.setImageBitmap(bitmap)
                binding.imbDelete.setOnClickListener {
                    list.removeAt(position)
                    notifyItemRemoved(position)
                }
            }
        }
    }
}