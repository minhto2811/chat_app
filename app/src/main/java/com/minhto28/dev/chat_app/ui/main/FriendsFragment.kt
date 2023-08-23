package com.minhto28.dev.chat_app.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.minhto28.dev.chat_app.R
import com.minhto28.dev.chat_app.adapters.InvitationAdapter
import com.minhto28.dev.chat_app.adapters.UserAdapter
import com.minhto28.dev.chat_app.databinding.FragmentFriendsBinding
import com.minhto28.dev.chat_app.models.User
import com.minhto28.dev.chat_app.utils.DataManager
import com.minhto28.dev.chat_app.utils.showMessage

class FriendsFragment : Fragment() {
    private var _binding: FragmentFriendsBinding? = null
    private val binding get() = _binding!!
    private lateinit var databaseReference: DatabaseReference
    private lateinit var listUser: ArrayList<User>
    private lateinit var listFriend: ArrayList<User>
    private lateinit var listInvitation: ArrayList<User>
    private lateinit var UID: String
    private lateinit var userAdapter: UserAdapter
    private lateinit var invitationAdapter: InvitationAdapter
    private var expand = true
    private val collapseDuration = 500
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendsBinding.inflate(inflater, container, false)
        databaseReference = Firebase.database.reference
        listUser = ArrayList()
        listFriend = ArrayList()
        listInvitation = ArrayList()
        UID = DataManager.getInstance().getUser()!!.uid!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        expandOrCollapse()
        getDataUser()
    }

    private fun expandOrCollapse() {
        binding.imvExpand.setOnClickListener {
            expand = !expand
            if (expand) {
                binding.imvExpand.setImageResource(R.drawable.expand_all_24px)

            } else {
                binding.imvExpand.setImageResource(R.drawable.collapse_all_24px)

            }
        }
    }

    private fun initView() {
        invitationAdapter = InvitationAdapter(listInvitation, UID)
        binding.rcvInvitation.adapter = invitationAdapter
        userAdapter = UserAdapter(listFriend, UID, true)
        binding.rcvFriends.adapter = userAdapter
        val divider = DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
        binding.rcvInvitation.addItemDecoration(divider)
        binding.rcvFriends.addItemDecoration(divider)
    }

    private fun getDataUser() {
        var max = 99999999L
        val invitation = databaseReference.child("user")
        invitation.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (max > snapshot.childrenCount) {
                    max = snapshot.childrenCount
                }
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    listUser.add(user)

                    if (listUser.size.toLong() == max) {
                        getFriends()
                        getInvation()
                    }
                }

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    for (i in 0 until listFriend.size) {
                        if (listFriend[i].uid == user!!.uid){
                            listFriend.set(i,user)
                            userAdapter.notifyItemChanged(i)
                            break
                        }
                    }
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {
                showMessage(
                    "Error! An error occurred. Please try again later",
                    requireContext(),
                    false,
                    null
                )
            }
        })
    }

    private fun getInvation() {
        var index = 0
        var max = 99999999L
        val invationRef = databaseReference.child("invitation").child(UID)
        invationRef.addChildEventListener(object : ChildEventListener {
            @SuppressLint("SetTextI18n")
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (max > snapshot.childrenCount) {
                    max = snapshot.childrenCount
                }
                index++
                val yourID = snapshot.getValue(String::class.java)
                if (yourID != null) {
                    for (user in listUser) {
                        if (user.uid == yourID) {
                            listInvitation.add(user)
                            invitationAdapter.notifyItemInserted(listInvitation.size - 1)
                            break
                        }
                    }

                }
                if (index.toLong() == max || listInvitation.size > 0) {
                    binding.lnInvitation.visibility = View.VISIBLE
                    binding.tvCountInvitaion.text =
                        "You received ${listInvitation.size} friend requests"
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val yourID = snapshot.getValue(String::class.java)
                if (yourID != null) {
                    for (i in 0 until listInvitation.size) {
                        if (listInvitation[i].uid == yourID) {
                            listInvitation.removeAt(i)
                            invitationAdapter.notifyItemRemoved(i)
                            if (listInvitation.isEmpty()){
                                binding.lnInvitation.visibility = View.GONE
                            }
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

    private fun getFriends() {
        val friendRef = databaseReference.child("user").child(UID).child("friends")
        friendRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val uid = snapshot.getValue(String::class.java)
                if (uid != null) {
                    for (user in listUser) {
                        if (user.uid == uid) {
                            listFriend.add(user)
                            userAdapter.notifyItemInserted(listFriend.size - 1)
                            break
                        }
                    }

                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val uid = snapshot.getValue(String::class.java)
                if (uid != null) {
                    for (i in 0 until listFriend.size) {
                        if (listFriend[i].uid == uid) {
                            listFriend.removeAt(i)
                            userAdapter.notifyItemRemoved(i)
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}