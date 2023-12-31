package com.minhto28.dev.chat_app.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.minhto28.dev.chat_app.R
import com.minhto28.dev.chat_app.databinding.UserItemBinding
import com.minhto28.dev.chat_app.models.User

class UserAdapter(
    private val myID: String
) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    var list = ArrayList<User>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    inner class UserViewHolder(val binding: UserItemBinding, val context: Context) :
        ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = UserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding, parent.context)
    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                Glide.with(context).load(this.avatar).into(binding.imvAvatar)
                binding.tvFullname.text = this.fullname
                binding.tvUid.text = "UID: ${this.uid}"
                binding.imvOnline.visibility = if (this.status) View.VISIBLE else View.GONE
                if (cache) {
                    binding.imvAddFriend.setImageResource(R.drawable.baseline_schedule_send_24)
                    binding.imvAddFriend.setOnClickListener {
                        binding.imvAddFriend.setImageResource(R.drawable.baseline_person_add_alt_1_24)
                        denyFriendInvitations(myID)
                    }
                } else {
                    binding.imvAddFriend.setImageResource(R.drawable.baseline_person_add_alt_1_24)
                    binding.imvAddFriend.setOnClickListener {
                        binding.imvAddFriend.setImageResource(R.drawable.baseline_schedule_send_24)
                        sendFriendInvitations(myID) {
                            if (!it) {
                                Toast.makeText(context, "Invitation failed", Toast.LENGTH_SHORT)
                                    .show()
                                binding.imvAddFriend.setImageResource(R.drawable.baseline_person_add_alt_1_24)
                            }
                        }
                    }
                }
            }
        }
    }
}

