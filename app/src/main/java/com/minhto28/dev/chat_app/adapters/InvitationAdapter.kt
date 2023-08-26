package com.minhto28.dev.chat_app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.minhto28.dev.chat_app.databinding.InvitationItemBinding
import com.minhto28.dev.chat_app.models.User

class InvitationAdapter(private val myID: String) :
    RecyclerView.Adapter<InvitationAdapter.InvationViewHolder>() {
    private var list = ArrayList<User>()

    fun setData(data: ArrayList<User>) {
        list = data
        notifyDataSetChanged()
    }

    inner class InvationViewHolder(val binding: InvitationItemBinding, val context: Context) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvationViewHolder {
        val binding =
            InvitationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InvationViewHolder(binding, parent.context)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: InvationViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                Glide.with(context).load(this.avatar).into(binding.imvAvatar)
                binding.tvFullname.text = "${this.fullname} sent you a friend request"
                binding.btnDeny.setOnClickListener {
                    this.denyFriendInvitations(myID)
                }
                binding.btnAccept.setOnClickListener {
                        this.addFriend(myID)
                }
            }
        }
    }

}