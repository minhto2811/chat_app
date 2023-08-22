package com.minhto28.dev.chat_app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.minhto28.dev.chat_app.databinding.UserItemBinding
import com.minhto28.dev.chat_app.models.User

class UserAdapter(private var list: ArrayList<User>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    inner class UserViewHolder(val binding: UserItemBinding, val context: Context) :
        ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = UserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding, parent.context)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                Glide.with(context).load(this.avatar).into(binding.imvAvatar)
                binding.tvFullname.text = this.fullname
                binding.tvUid.text = "UID: ${this.uid}"
                binding.imvOnline.visibility = if (this.status) View.VISIBLE else View.GONE
            }
        }
    }
}