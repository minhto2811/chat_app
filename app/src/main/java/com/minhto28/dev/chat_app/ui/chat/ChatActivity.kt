package com.minhto28.dev.chat_app.ui.chat

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import com.minhto28.dev.chat_app.R
import com.minhto28.dev.chat_app.adapters.MessageAdapter
import com.minhto28.dev.chat_app.databinding.ActivityChatBinding
import com.minhto28.dev.chat_app.models.Message
import com.minhto28.dev.chat_app.models.User
import com.minhto28.dev.chat_app.utils.hiddenSoftKeyboard
import com.minhto28.dev.chat_app.utils.showMessage

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var listMessage: ArrayList<Message>
    private lateinit var messageAdapter: MessageAdapter
    private val PERMISSION_REQUEST_CODE = 555
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        databaseReference = Firebase.database.reference
        listMessage = ArrayList()
        val id = intent.getStringExtra("id")
        val UID = intent.getStringExtra("UID")
        if (id != null && UID != null) {
            setInfoFriend(UID, id)
            showChat(UID, id)
            sendMessage(UID, id)
            initView(UID)
            createPopupMenu(UID, id)
            allowSend()
            gallery()
        } else {
            showMessage("Error! An error occurred. Please try again later", this, false) {
                finish()
            }
        }

    }

    private fun gallery() {
        binding.imvGallery.setOnClickListener {
            checkPermission()
        }
    }

    private fun checkPermission() {

    }

    private fun oppenGallery() {
        val mimeTypes = arrayOf("image/*", "video/*") // Chọn cả ảnh và video
        imagePickerLauncher.launch(mimeTypes.toString())
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                oppenGallery()
            } else {
                showMessage("Access denied", this, false, null)
            }
        }
    }

    private fun createPopupMenu(UID: String, id: String) {
        binding.imvMoreVert.setOnClickListener {
            val popupMenu = PopupMenu(this, it) // "this" là Context, "button" là View
            popupMenu.inflate(R.menu.menu_action_user) // Gắn menu resource vào PopupMenu

            // Xử lý sự kiện khi một mục được chọn
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_delete_chat -> {
                        clearChat(UID, id) { finish() }
                        true
                    }

                    R.id.action_delete_friend -> {
                        clearChat(UID, id) {
                            clearChat(id, UID) {
                                deleteFriend(UID, id) {
                                    deleteFriend(id, UID) {
                                        finish()
                                    }
                                }
                            }
                        }

                        true
                    }

                    else -> false
                }
            }

            popupMenu.show() // Hiển thị PopupMenu
        }
    }

    private fun deleteFriend(id_sender: String, id_reciver: String, callback: (() -> Unit)?) {
        databaseReference.child("user").child(id_sender).child("friends").child(id_reciver)
            .removeValue().addOnSuccessListener {
                callback?.invoke()
            }
    }

    private fun clearChat(id_sender: String, id_reciver: String, callback: (() -> Unit)?) {
        databaseReference.child("chat").child(id_sender).child(id_reciver).removeValue()
            .addOnSuccessListener {
                callback?.invoke()
            }
    }


    private fun initView(UID: String) {
        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                if (uri != null) {
                    // Thực hiện xử lý với uri
                }
            }
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

    private fun setInfoFriend(UID: String, id: String) {
        val friendRef = databaseReference.child("user").child(id)
        friendRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val friend = snapshot.getValue(User::class.java)
                if (friend != null) {
                    Glide.with(applicationContext).load(friend.avatar).into(binding.imvAvatar)
                    binding.tvFullname.text = friend.fullname
                    binding.imvOnline.visibility = if (friend.status) View.VISIBLE else View.GONE
                    if (friend.friends?.get(UID) == null) {
                        finish()
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                showMessage(
                    "Error! An error occurred. Please try again later",
                    this@ChatActivity,
                    false
                ) {
                    finish()
                }
            }

        })
    }


    override fun onCreateContextMenu(
        menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.menu_action_user, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return super.onContextItemSelected(item)
        when (item.itemId) {
            R.id.action_delete_chat -> Toast.makeText(this, "1", Toast.LENGTH_SHORT).show()
            R.id.action_delete_friend -> Toast.makeText(this, "2", Toast.LENGTH_SHORT).show()
        }
    }


}