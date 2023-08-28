package com.minhto28.dev.chat_app.ui.chat

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.minhto28.dev.chat_app.models.Message
import com.minhto28.dev.chat_app.models.User
import com.minhto28.dev.chat_app.ui.main.dataFriend
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.TreeMap

class ChatViewModel() : ViewModel() {

    private val databaseReference = Firebase.database.reference
    var friend = MutableLiveData<User?>()
    var chat = MutableLiveData<ArrayList<Message>>()
    private var chatMap = TreeMap<Long, Message>()
    private val storage = FirebaseStorage.getInstance().reference
    private val imageMutex = Mutex()
    fun setFriend(uid: String, context: ChatActivity) {
        dataFriend.observe(context) {
            friend.postValue(it[uid])
        }
    }

    fun showChat(UID: String, id: String) {

        val chatRef = databaseReference.child("chat").child(UID).child(id)
        chatRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                if (message != null) {
                    chatMap[message.time!!] = message
                    chat.postValue(ArrayList(chatMap.values))
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                if (message != null) {
                    chatMap[message.time!!] = message
                    chat.postValue(ArrayList(chatMap.values))
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val message = snapshot.getValue(Message::class.java)
                if (message != null) {
                    chatMap.remove(message.time)
                    chat.postValue(ArrayList(chatMap.values))
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    fun save(
        id_sender: String,
        id_reciver: String,
        message: Message,
        uris: ArrayList<Uri>?,
        callback: ((Boolean) -> Unit)?
    ) {
        CoroutineScope(Dispatchers.Default).launch {
            uris?.let { uris ->
                uploadMultipleFile(uris) {
                    Log.e("list uri", it.toString())
                    if (!it.isNullOrEmpty()) {
                        message.image = it
                    }
                }
            }
            val yourChat = databaseReference.child("chat").child(id_reciver).child(id_sender)
                .child(message.time.toString())
            yourChat.setValue(message)

            val myChat = databaseReference.child("chat").child(id_sender).child(id_reciver)
                .child(message.time.toString())
            myChat.setValue(message).addOnFailureListener {
                callback?.invoke(false)
            }.addOnSuccessListener {
                callback?.invoke(true)
            }
        }
    }

    suspend fun uploadMultipleFile(
        fileUri: ArrayList<Uri>,
        onResult: (List<String>?) -> Unit
    ) {
        try {
            val uri: List<String> = withContext(Dispatchers.IO) {
                fileUri.map { image ->
                    async {
                        storage.child("chat/${image.lastPathSegment}-${System.currentTimeMillis()}.jpg")
                            .putFile(image)
                            .await()
                            .storage
                            .downloadUrl
                            .await().toString()
                    }
                }.awaitAll()
            }
            onResult.invoke(uri)
        } catch (e: FirebaseException) {
            onResult.invoke(null)
        } catch (e: Exception) {
            onResult.invoke(null)
        }
    }


    fun clearChat(id_sender: String, id_reciver: String, callback: (() -> Unit)?) {
        databaseReference.child("chat").child(id_sender).child(id_reciver).removeValue()
            .addOnSuccessListener {
                callback?.invoke()
            }
    }

    fun deleteFriend(
        id_sender: String, id_reciver: String, callback: (() -> Unit)?
    ) {
        databaseReference.child("user").child(id_sender).child("friends").child(id_reciver)
            .removeValue().addOnSuccessListener {
                callback?.invoke()
            }
    }

}