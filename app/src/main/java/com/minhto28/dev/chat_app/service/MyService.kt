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
    private lateinit var user: User
    private var databaseReference: DatabaseReference = Firebase.database.reference
    private var listAll = HashMap<String, User>()
    private var listHome = HashMap<String, User>()
    private var listFriend = HashMap<String, User>()
    private var listInvitation = HashMap<String, User>()


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        user = DataManager.getInstance().getUser()!!
        user.setStatusOnline(true)
        fetchData(user.uid!!)
        return START_NOT_STICKY
    }


    fun fetchData(uid: String) {
        databaseReference.child("user").addChildEventListener(object : ChildEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                val userMore = snapshot.getValue(User::class.java)
                if (userMore != null) {
                    if (userMore.uid != uid) {
                        listAll[userMore.uid!!] = userMore
                        listHome[userMore.uid!!] = userMore
                        dataHome.postValue(ArrayList(listHome.values))
                        getFriends(uid)
                        getInvitation(uid)

                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    val boolean = listHome[user.uid!!] != null
                    val boolean1 = listFriend[user.uid!!] == null
                    if (boolean && boolean1) {
                        listHome[user.uid!!] = user
                        dataHome.postValue(ArrayList(listHome.values))
                    }
                }

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    val boolean = listAll[user.uid!!] != null
                    if (boolean) {
                        listAll.remove(user.uid!!)
                    }
                    val boolean1 = listHome[user.uid!!] != null
                    if (boolean1) {
                        listHome.remove(user.uid!!)
                        dataHome.postValue(ArrayList(listHome.values))
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
                        listInvitation[id] = listAll[id]!!
                        dataInvitation.postValue(ArrayList(listInvitation.values))
                        MainActivity.setCount(R.id.friendsFragment, listInvitation.size)
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    val id = snapshot.getValue(String::class.java)
                    if (id != null) {
                        listInvitation.remove(id)
                        dataInvitation.postValue(ArrayList(listInvitation.values))
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
                if (id != null) {
                    listFriend[id] = listAll[id]!!
                    listHome.remove(id)
                    dataFriend.postValue(ArrayList(listFriend.values))
                    dataHome.postValue(ArrayList(listHome.values))
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val id = snapshot.getValue(String::class.java)
                if (id != null) {
                    listFriend[id] = listAll[id]!!
                    dataFriend.postValue(ArrayList(listFriend.values))
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val id = snapshot.getValue(String::class.java)
                if (id != null) {
                    listFriend.remove(id)
                    listHome[id] = listAll[id]!!
                    dataFriend.postValue(ArrayList(listFriend.values))
                    dataHome.postValue(ArrayList(listHome.values))
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


    override fun onTaskRemoved(rootIntent: Intent?) {
        user.setStatusOnline(false)
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        user.setStatusOnline(false)
    }


}
