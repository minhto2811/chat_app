package com.minhto28.dev.chat_app.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.minhto28.dev.chat_app.R
import com.minhto28.dev.chat_app.models.Friend
import com.minhto28.dev.chat_app.models.User
import com.minhto28.dev.chat_app.ui.main.MainActivity
import com.minhto28.dev.chat_app.ui.main.USER
import com.minhto28.dev.chat_app.ui.main.dataFriend
import com.minhto28.dev.chat_app.ui.main.dataHome
import com.minhto28.dev.chat_app.ui.main.dataInvitation


class MyService : Service() {

    override fun onBind(intent: Intent): IBinder? = null
    private var user: User = USER.value!!
    private var databaseReference: DatabaseReference = Firebase.database.reference
    private var listHome = HashMap<String, User>()
    private var listFriend = HashMap<String, Friend>()
    private var listInvitation = HashMap<String, User>()


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        user.setStatusOnline(true)
        fetchData(user.uid)
        return START_NOT_STICKY
    }


    private fun fetchData(uid: String) {
        databaseReference.child("user").addChildEventListener(object : ChildEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                try {
                    val userMore = snapshot.getValue(User::class.java)
                    if (userMore != null && userMore.uid != uid) {
                        listHome[userMore.uid] = userMore
                        dataHome.postValue(listHome)
                        getFriends(uid)
                        getInvitation(uid)
                        getCache(uid)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    if (user.uid == uid) {
                        USER.postValue(user)
                        return
                    }
                    if (listFriend[user.uid] != null) {
                        listFriend[user.uid]!!.user = user
                        dataFriend.postValue(listFriend)
                    } else if (listHome[user.uid] != null) {
                        listHome[user.uid] = user
                        dataHome.postValue(listHome)
                    }
                }

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    if (listHome[user.uid] != null) {
                        listHome.remove(user.uid)
                        dataHome.postValue(listHome)
                    } else if (listFriend[user.uid] != null) {
                        listFriend.remove(user.uid)
                        dataFriend.postValue(listFriend)
                    }
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun getCache(uid: String) {
        databaseReference.child("cache").child(uid)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val id = snapshot.getValue(String::class.java)
                    if (id != null && listHome[id] != null) {
                        listHome[id]!!.cache = true
                        dataHome.postValue(listHome)
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    val id = snapshot.getValue(String::class.java)
                    if (id != null && listHome[id] != null) {
                        listHome[id]!!.cache = false
                        dataHome.postValue(listHome)
                    }
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    private fun getInvitation(uid: String) {
        databaseReference.child("invitation").child(uid)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val id = snapshot.getValue(String::class.java)
                    if (id != null) {
                        listInvitation[id] = listHome[id]!!
                        dataInvitation.postValue(listInvitation)
                        MainActivity.addCount(R.id.friendsFragment, 0, listInvitation.size)
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    val id = snapshot.getValue(String::class.java)
                    if (id != null) {
                        listInvitation.remove(id)
                        dataInvitation.postValue(listInvitation)
                        MainActivity.addCount(R.id.friendsFragment, 0, listInvitation.size)
                    }
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    private fun getFriends(uid: String) {
        val friendRef = databaseReference.child("friend").child(uid)
        friendRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val friend = snapshot.getValue(Friend::class.java)
                if (friend != null && listHome[friend.idFriend] != null) {
                    friend.user = listHome[friend.idFriend]
                    listFriend[friend.idFriend] = friend
                    dataFriend.postValue(listFriend)
                    listHome.remove(friend.idFriend)
                    dataHome.postValue(listHome)
                    MainActivity.addCount(
                        R.id.friendsFragment,
                        friend.count,
                        listInvitation.size
                    )
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val friend = snapshot.getValue(Friend::class.java)
                if (friend != null && listFriend[friend.idFriend] != null) {
                    MainActivity.addCount(
                        R.id.friendsFragment,
                        friend.count - listFriend[friend.idFriend]!!.count,
                        listInvitation.size
                    )
                    friend.user = listFriend[friend.idFriend]!!.user
                    listFriend[friend.idFriend] = friend
                    dataFriend.postValue(listFriend)
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val friend = snapshot.getValue(Friend::class.java)
                if (friend != null && listFriend[friend.idFriend] != null) {
                    MainActivity.addCount(
                        R.id.friendsFragment,
                      0-  friend.count,
                        listInvitation.size
                    )
                    listHome[friend.idFriend] = listFriend[friend.idFriend]!!.user!!
                    listFriend.remove(friend.idFriend)
                    dataFriend.postValue(listFriend)
                    dataHome.postValue(listHome)
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }



    override fun onDestroy() {
        super.onDestroy()
        user.setStatusOnline(false)
    }

}
