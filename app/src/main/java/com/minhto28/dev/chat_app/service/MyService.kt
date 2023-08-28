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
import com.minhto28.dev.chat_app.models.User
import com.minhto28.dev.chat_app.ui.main.MainActivity
import com.minhto28.dev.chat_app.ui.main.dataFriend
import com.minhto28.dev.chat_app.ui.main.dataHome
import com.minhto28.dev.chat_app.ui.main.dataInvitation
import com.minhto28.dev.chat_app.utils.DataManager

class MyService : Service() {

    override fun onBind(intent: Intent): IBinder? = null
    private var user: User? = DataManager.getInstance().getUser()
    private var databaseReference: DatabaseReference = Firebase.database.reference
    private var listHome = HashMap<String, User>()
    private var listFriend = HashMap<String, User>()
    private var listInvitation = HashMap<String, User>()


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        user?.setStatusOnline(true)
        fetchData(user?.uid!!)
        return START_NOT_STICKY
    }


    private fun fetchData(uid: String) {
        databaseReference.child("user").addChildEventListener(object : ChildEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                try {
                    val userMore = snapshot.getValue(User::class.java)
                    if (userMore != null) {
                        if (userMore.uid != uid) {
                            listHome[userMore.uid!!] = userMore
                            dataHome.postValue(listHome)
                            getFriends(uid)
                            getInvitation(uid)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    val boolean = listHome[user.uid] != null
                    val boolean1 = listFriend[user.uid] != null
                    if (boolean1) {
                        listFriend[user.uid!!] = user
                        dataFriend.postValue(listFriend)
                    } else if (boolean) {
                        listHome[user.uid!!] = user
                        dataHome.postValue(listHome)
                    }
                }

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    val boolean1 = listHome[user.uid] != null
                    if (boolean1) {
                        listHome.remove(user.uid)
                        dataHome.postValue(listHome)
                    }
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
                        MainActivity.setCount(R.id.friendsFragment, listInvitation.size)
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    val id = snapshot.getValue(String::class.java)
                    if (id != null) {
                        listInvitation.remove(id)
                        dataInvitation.postValue(listInvitation)
                        if (listInvitation.isEmpty()) {
                            MainActivity.clearCount(R.id.friendsFragment)
                        } else {
                            MainActivity.setCount(R.id.friendsFragment, listInvitation.size)
                        }
                    }
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    private fun getFriends(uid: String) {
        val friendRef = databaseReference.child("user").child(uid).child("friends")
        friendRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val id = snapshot.getValue(String::class.java)
                if (id != null && listHome[id] != null) {
                    listFriend[id] = listHome[id]!!
                    dataFriend.postValue(listFriend)
                    listHome.remove(id)
                    dataHome.postValue(listHome)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val id = snapshot.getValue(String::class.java)
                if (id != null && listFriend[id] != null) {
                    listHome[id] = listFriend[id]!!
                    listFriend.remove(id)
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


    override fun onTaskRemoved(rootIntent: Intent?) {
        user?.setStatusOnline(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        user?.setStatusOnline(false)
    }

}
