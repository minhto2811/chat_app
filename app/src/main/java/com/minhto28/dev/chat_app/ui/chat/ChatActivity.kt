package com.minhto28.dev.chat_app.ui.chat

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.minhto28.dev.chat_app.R
import com.minhto28.dev.chat_app.adapters.ImageAdapter
import com.minhto28.dev.chat_app.adapters.MessageAdapter
import com.minhto28.dev.chat_app.databinding.ActivityChatBinding
import com.minhto28.dev.chat_app.models.Message
import com.minhto28.dev.chat_app.utils.hiddenSoftKeyboard
import com.minhto28.dev.chat_app.utils.showMessage


class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding

    private lateinit var messageAdapter: MessageAdapter
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var imageAdapter: ImageAdapter
    private var id: String? = null

    private val pickImageActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val uri = ArrayList<Uri>()
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data?.clipData != null) {
                    val count = data.clipData!!.itemCount
                    for (i in 0 until count) {
                        uri.add(data.clipData!!.getItemAt(i).uri)
                    }
                } else if (data?.data != null) {
                    uri.add(data.data!!)
                }
                uri.isNotEmpty().let { notEmpty ->
                    if (notEmpty) {
                        imageAdapter.list = uri
                    }
                }
                binding.imvSend.enable()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        id = intent.getStringExtra("id")
        val myID = intent.getStringExtra("UID")
        if (id != null && myID != null) {
            initView(myID)
            setInfoFriend(id!!)
            chatViewModel.showChat(myID, id!!)
            chatViewModel.chat.observe(this) {
                messageAdapter.list = it
                if (it.size > 0) {
                    binding.scrollMessage.fullScroll(View.FOCUS_DOWN)
                }
            }
            chatViewModel.setSeeding(id!!, true)

            sendMessage(myID, id!!)
            createPopupMenu(id!!)
            allowSend()
            gallery()
            binding.imvBack.setOnClickListener {
                finish()
            }
        } else {
            showMessage("Error! An error occurred. Please try again later", this, false) {
                finish()
            }
        }

    }


    private fun gallery() {
        binding.imvGallery.setOnClickListener {
            selectImage()
        }
    }


    private fun selectImage() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        pickImageActivityResult.launch(intent)
    }


    private fun createPopupMenu(id: String) {
        binding.imvMoreVert.setOnClickListener {
            val popupMenu = PopupMenu(this, it) // "this" là Context, "button" là View
            popupMenu.inflate(R.menu.menu_action_user) // Gắn menu resource vào PopupMenu
            // Xử lý sự kiện khi một mục được chọn
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_delete_chat -> {
                        chatViewModel.clearChat(id, true) { finish() }
                        true
                    }

                    R.id.action_delete_friend -> {
                        chatViewModel.deleteFriend(id) {
                            finish()
                        }

                        true
                    }

                    else -> false
                }
            }

            popupMenu.show() // Hiển thị PopupMenu
        }
    }


    private fun initView(myID: String) {
        messageAdapter = MessageAdapter(myID)
        binding.rcvChat.adapter = messageAdapter
        imageAdapter = ImageAdapter()
        binding.rcvImage.adapter = imageAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun sendMessage(myID: String, id: String) {
        binding.imvSend.setOnClickListener {
            binding.lnSending.visibility = View.VISIBLE
            val mess = binding.edtText.text.toString().trim()
            val message = Message(System.currentTimeMillis(), mess, null, myID, null)


            chatViewModel.save(myID, id, message, imageAdapter.list) {
                binding.lnSending.visibility = View.GONE
                imageAdapter.list.clear()
                imageAdapter.notifyDataSetChanged()
                binding.rcvImage.visibility = View.VISIBLE
                if (!it) {
                    Toast.makeText(this, "Your message has not been sent", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            binding.rcvImage.visibility = View.GONE
            binding.edtText.text = null
            binding.edtText.clearFocus()
            hiddenSoftKeyboard(this)
            binding.imvSend.visibility = View.GONE
        }
    }


    private fun allowSend() {
        binding.edtText.addTextChangedListener {
            binding.imvSend.enable()
        }
    }

    private fun ImageView.enable() {
        visibility = if (binding.edtText.text.toString().trim()
                .isNotEmpty() || imageAdapter.list.isNotEmpty()
        ) View.VISIBLE else View.GONE
    }


    private fun setInfoFriend(id: String) {
        chatViewModel.setFriend(id, this)
        chatViewModel.friend.observe(this) {
            if (it != null) {
                Glide.with(applicationContext).load(it.avatar).into(binding.imvAvatar)
                binding.tvFullname.text = it.fullname
                binding.imvOnline.visibility = if (it.status) View.VISIBLE else View.GONE
            } else {
                showMessage("The chat is no longer available", this, false) {
                    finish()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (id != null) {
            chatViewModel.setSeeding(id!!, true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (id != null) {
            chatViewModel.setSeeding(id!!, false)
        }
    }
}