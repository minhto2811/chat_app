package com.minhto28.dev.chat_app.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.minhto28.dev.chat_app.R
import com.minhto28.dev.chat_app.databinding.ReciverLayoutBinding
import com.minhto28.dev.chat_app.databinding.SenderLayoutBinding
import com.minhto28.dev.chat_app.models.Message
import com.minhto28.dev.chat_app.utils.getTimeDisplay

class MessageAdapter(private val myID: String) : RecyclerView.Adapter<ViewHolder>() {
    var list = ArrayList<Message>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemViewType(position: Int): Int {
        val message = list[position]
        if (message.uid == myID) {
            return R.layout.sender_layout
        }
        return R.layout.reciver_layout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == R.layout.sender_layout) {
            val binding =
                SenderLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            SenderViewholder(binding, parent.context)
        } else {
            val binding =
                ReciverLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ReciverViewHolder(binding, parent.context)
        }
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = list[position]
        val currentTime: Long = System.currentTimeMillis()
        when (holder) {
            is SenderViewholder -> {
                if (message.message.isNullOrEmpty()) {
                    holder.binding.tvMess.visibility = View.GONE
                } else {
                    holder.binding.tvMess.text = message.message
                }

                if (!message.image.isNullOrEmpty()) {
                    holder.binding.rcvImage.adapter = MessegeImageAdapter(message.image!!)
                    holder.binding.rcvImage.setHasFixedSize(true)
                } else {
                    holder.binding.rcvImage.visibility = View.GONE
                }

                if (currentTime - message.time!! > 300000) {
                    holder.binding.tvTime.text = getTimeDisplay(message.time)
                } else {
                    holder.binding.tvTime.visibility = View.GONE
                }

            }

            is ReciverViewHolder -> {
                if (message.message.isNullOrEmpty()) {
                    holder.binding.tvMess.visibility = View.GONE
                } else {
                    holder.binding.tvMess.text = message.message
                }

                if (!message.image.isNullOrEmpty()) {
                    holder.binding.rcvImage.adapter = MessegeImageAdapter(message.image!!)
                    holder.binding.rcvImage.setHasFixedSize(true)
                } else {
                    holder.binding.rcvImage.visibility = View.GONE
                }

                if (currentTime - message.time!! > 300000) {
                    holder.binding.tvTime.text = getTimeDisplay(message.time)
                } else {
                    holder.binding.tvTime.visibility = View.GONE
                }
            }
        }
    }

    class SenderViewholder(val binding: SenderLayoutBinding, val context: Context) :
        ViewHolder(binding.root)

    class ReciverViewHolder(val binding: ReciverLayoutBinding, val context: Context) :
        ViewHolder(binding.root)
}