package com.minhto28.dev.chat_app.service

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
import com.minhto28.dev.chat_app.utils.getSerializable

class MyService : Service() {

    override fun onBind(intent: Intent): IBinder? = null
    private lateinit var user: User
    private lateinit var databaseReference: DatabaseReference
    private val hashMap: HashMap<String, String> = HashMap()

    override fun onCreate() {
        super.onCreate()
        databaseReference = Firebase.database.reference
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        user = intent?.getSerializable("user", User::class.java)!!
        user.setStatusOnline(true)
        notifictionInvitation(user.uid!!)
        return START_NOT_STICKY
    }

    private fun notifictionInvitation(uid: String) {

        databaseReference.child("invitation").child(uid)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val id = snapshot.getValue(String::class.java)
                    if (id != null) {
                        hashMap[id] = id
                        MainActivity.setCount(R.id.friendsFragment, hashMap.size)
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    val id = snapshot.getValue(String::class.java)
                    if (id != null) {
                        hashMap.remove(id)
                        if (hashMap.isEmpty()) {
                            MainActivity.clearCount(R.id.friendsFragment)
                        }
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

}
