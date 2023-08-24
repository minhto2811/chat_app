package com.minhto28.dev.chat_app.adapters

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

class MessageAdapter(private val list: ArrayList<Message>, private val myID: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        val message = list[position]
        if (message.uid == myID) {
            return R.layout.sender_layout
        }
        return R.layout.reciver_layout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == R.layout.sender_layout) {
            val binding =
                SenderLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            SenderViewholder(binding)
        } else {
            val binding =
                ReciverLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ReciverViewHolder(binding)
        }
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = list[position]
        val index_time: Long = System.currentTimeMillis()
        when (holder) {
            is SenderViewholder -> {
                holder.binding.tvMess.text = message.message
                if (index_time - message.time!! > 300000) {
                    holder.binding.tvTime.visibility = View.VISIBLE
                    holder.binding.tvTime.text = getTimeDisplay(message.time)
                }
            }

            is ReciverViewHolder -> {
                holder.binding.tvMess.text = message.message
                if (index_time - message.time!! > 300000) {
                    holder.binding.tvTime.visibility = View.VISIBLE
                    holder.binding.tvTime.text = getTimeDisplay(message.time)
                }
            }
        }
    }

    class SenderViewholder(val binding: SenderLayoutBinding) : ViewHolder(binding.root)
    class ReciverViewHolder(val binding: ReciverLayoutBinding) : ViewHolder(binding.root)
}