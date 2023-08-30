package com.minhto28.dev.chat_app.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.minhto28.dev.chat_app.R
import com.minhto28.dev.chat_app.databinding.FriendItemBinding
import com.minhto28.dev.chat_app.models.Friend
import com.minhto28.dev.chat_app.ui.chat.ChatActivity
import com.minhto28.dev.chat_app.utils.clickEffect

class FriendAdapter(
    private val myID: String,
) : RecyclerView.Adapter<FriendAdapter.FriendViewHolder>() {
    var list = ArrayList<Friend>()
        @SuppressLint("NotifyDataSetChanged") set(value) {
            field = value
            notifyDataSetChanged()
        }


    inner class FriendViewHolder(val binding: FriendItemBinding, val context: Context) :
        ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val binding = FriendItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FriendViewHolder(binding, parent.context)
    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                Glide.with(context).load(user!!.avatar).into(binding.imvAvatar)
                binding.tvFullname.text = user!!.fullname
                binding.imvOnline.visibility = if (user!!.status) View.VISIBLE else View.GONE

                if (this.count > 0) {
                    binding.lavNotification.visibility = View.VISIBLE
                    binding.tvUid.setTextColor(ContextCompat.getColor(context, R.color.black))
                } else {
                    binding.lavNotification.visibility = View.GONE
                    binding.tvUid.setTextColor(ContextCompat.getColor(context, R.color.hint))
                }

                if (lastMessage.isNullOrEmpty()) {
                    binding.tvUid.text = "UID: ${this.user!!.uid}"
                    binding.tvUid.setTextColor(ContextCompat.getColor(context, R.color.orange))
                } else {
                    binding.tvUid.text = lastMessage
                }




                binding.lnChat.setOnClickListener {
                    binding.lnChat.clickEffect()
                    val intent = Intent(context, ChatActivity::class.java)
                    intent.putExtra("id", this.user!!.uid)
                    intent.putExtra("UID", myID)
                    context.startActivity(intent)
                }
            }
        }
    }


}