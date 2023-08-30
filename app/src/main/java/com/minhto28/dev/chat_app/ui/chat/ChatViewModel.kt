package com.minhto28.dev.chat_app.ui.chat

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.minhto28.dev.chat_app.models.Friend
import com.minhto28.dev.chat_app.models.Message
import com.minhto28.dev.chat_app.models.User
import com.minhto28.dev.chat_app.ui.main.USER
import com.minhto28.dev.chat_app.ui.main.dataFriend
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.TreeMap

class ChatViewModel : ViewModel() {

    private val databaseReference = Firebase.database.reference
    var friend = MutableLiveData<User?>()
    var chat = MutableLiveData<ArrayList<Message>>()
    private var chatMap = TreeMap<Long, Message>()
    private val storage = FirebaseStorage.getInstance().reference
    fun setFriend(uid: String, context: ChatActivity) {
        dataFriend.observe(context) {
            friend.postValue(it[uid]?.user)
        }
    }

    fun showChat(myId: String, id: String) {

        val chatRef = databaseReference.child("chat").child(myId).child(id)
        chatRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                if (message != null) {
                    chatMap[message.time] = message
                    chat.postValue(ArrayList(chatMap.values))
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                if (message != null) {
                    chatMap[message.time] = message
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
        idSender: String,
        idReciver: String,
        message: Message,
        uris: ArrayList<Uri>?,
        callback: ((Boolean) -> Unit)?
    ) {
        val id = idSender + idReciver
        val job = Job()
        val scope = CoroutineScope(Dispatchers.IO + job + CoroutineName("send message"))
        scope.launch {
            uris?.let { uris ->
                uploadMultipleFile(id, uris) {
                    if (!it.isNullOrEmpty()) {
                        message.image = it
                    }
                }
            }
            val yourChat = databaseReference.child("chat").child(idReciver).child(idSender)
                .child(message.time.toString())
            yourChat.setValue(message)

            val myChat = databaseReference.child("chat").child(idSender).child(idReciver)
                .child(message.time.toString())
            myChat.setValue(message).addOnFailureListener {
                callback?.invoke(false)
            }.addOnSuccessListener {
                callback?.invoke(true)
                setNotificationMessage(idSender, idReciver, message.message)
            }
        }
    }

    private fun setNotificationMessage(idSender: String, idReciver: String, message: String?) {
        val notyRef = databaseReference.child("friend").child(idReciver).child(idSender)
        notyRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val friend = snapshot.getValue(Friend::class.java)
                if (friend != null && friend.seending == false) {
                    val count = friend.count + 1
                    notyRef.child("count").setValue(count)
                    val mess = if (message.isNullOrEmpty()) "Hình ảnh" else message
                    notyRef.child("lastMessage").setValue(mess)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private suspend fun uploadMultipleFile(
        id: String, fileUri: ArrayList<Uri>, onResult: (List<String>?) -> Unit
    ) {
        try {
            val uri: List<String> = withContext(Dispatchers.IO) {
                fileUri.map { image ->
                    async {
                        storage.child("chat/$id/${image.lastPathSegment}-${System.currentTimeMillis()}.jpg")
                            .putFile(image).await().storage.downloadUrl.await().toString()
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


    fun clearChat(idReciver: String, only: Boolean, callback: () -> Unit) {
        USER.value!!.clearChat(idReciver, only) {
            callback.invoke()
        }
    }

    fun deleteFriend(
        idReciver: String, callback: (() -> Unit)
    ) {
        USER.value!!.deleteFriend(idReciver) {
            callback.invoke()
        }
    }

    fun setSeeding(id: String, isSeeding: Boolean) {
        USER.value?.setSeeding(id, isSeeding)
    }

}