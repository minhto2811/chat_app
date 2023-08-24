package com.minhto28.dev.chat_app.ui.chat

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.minhto28.dev.chat_app.adapters.MessageAdapter
import com.minhto28.dev.chat_app.databinding.ActivityChatBinding
import com.minhto28.dev.chat_app.models.Message
import com.minhto28.dev.chat_app.models.User
import com.minhto28.dev.chat_app.utils.hiddenSoftKeyboard

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var listMessage: ArrayList<Message>
    private lateinit var messageAdapter: MessageAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        databaseReference = Firebase.database.reference
        listMessage = ArrayList()
        val id = intent.getStringExtra("id")
        val UID = intent.getStringExtra("UID")
        if (id != null && UID != null) {
            setInfoFriend(id)
            showChat(UID, id)
            sendMessage(UID, id)
            initView(UID)
        }
        allowSend()
    }

    private fun initView(UID: String) {
        messageAdapter = MessageAdapter(listMessage, UID)
        binding.rcvChat.adapter = messageAdapter
    }

    private fun sendMessage(UID: String, id: String) {
        binding.imvSend.setOnClickListener {
            val mess = binding.edtText.text.toString().trim()
            binding.edtText.text = null
            binding.edtText.clearFocus()
            hiddenSoftKeyboard(this)
            val time = System.currentTimeMillis()
            val message = Message(time, mess, null, UID, null)
            save(UID, id, message) {
                Toast.makeText(this, "Your message has not been sent", Toast.LENGTH_SHORT).show()
            }
            save(id, UID, message, null)
        }
    }

    private fun save(
        id_sender: String,
        id_reciver: String,
        message: Message,
        callback: (() -> Unit)?
    ) {
        val myChat =
            databaseReference.child("chat").child(id_sender).child(id_reciver)
                .child(message.time.toString())
        myChat.setValue(message).addOnFailureListener {
            callback?.invoke()
        }
    }

    private fun allowSend() {
        binding.edtText.addTextChangedListener {
            binding.imvSend.visibility =
                if (it.toString().trim().isNotEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun showChat(UID: String, id: String) {
        val chatRef = databaseReference.child("chat").child(UID).child(id)
        chatRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                if (message != null) {
                    listMessage.add(message)
                    messageAdapter.notifyItemInserted(listMessage.size - 1)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                if (message != null) {
                    for (i in 0 until listMessage.size) {
                        if (listMessage[i].time == message.time) {
                            listMessage[i] = message
                            messageAdapter.notifyItemChanged(i)
                            break
                        }
                    }

                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val message = snapshot.getValue(Message::class.java)
                if (message != null) {
                    for (i in 0 until listMessage.size) {
                        if (listMessage[i].time == message.time) {
                            listMessage.removeAt(i)
                            messageAdapter.notifyItemRemoved(i)
                            break
                        }
                    }
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun setInfoFriend(id: String) {
        val friendRef = databaseReference.child("user").child(id)
        friendRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val friend = snapshot.getValue(User::class.java)
                if (friend != null) {
                    Glide.with(applicationContext).load(friend.avatar).into(binding.imvAvatar)
                    binding.tvFullname.text = friend.fullname
                    binding.imvOnline.visibility = if (friend.status) View.VISIBLE else View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                binding.tvFullname.text = "Người dùng ChatNow"
                binding.imvOnline.visibility = View.GONE
            }

        })
    }


}