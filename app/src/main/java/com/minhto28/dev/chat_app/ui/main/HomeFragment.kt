package com.minhto28.dev.chat_app.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.minhto28.dev.chat_app.adapters.UserAdapter
import com.minhto28.dev.chat_app.databinding.FragmentHomeBinding
import com.minhto28.dev.chat_app.models.User
import com.minhto28.dev.chat_app.utils.DataManager
import com.minhto28.dev.chat_app.utils.showMessage


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var databaseReference: DatabaseReference
    private lateinit var list: ArrayList<User>
    private lateinit var data: ArrayList<User>
    private lateinit var userAdapter: UserAdapter
    private lateinit var UID: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        databaseReference = Firebase.database.reference
        list = ArrayList()
        data = ArrayList()
        UID = DataManager.getInstance().getUser()!!.uid!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inintView()
        getData()
        searchUser()
    }

    private fun searchUser() {
        binding.searchUser.clearFocus()
        binding.searchUser.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchByQuery(newText!!.trim())
                return false
            }

        })
    }



    private fun inintView() {
        userAdapter = UserAdapter(list, UID)
        binding.rcvUser.adapter = userAdapter
        val divider = DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
        binding.rcvUser.addItemDecoration(divider)
    }

    private fun getFriends() {
        val friendRef = databaseReference.child("user").child(UID).child("friends")
        friendRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val uid = snapshot.getValue(String::class.java)
                if (uid != null) {
                    for (i in 0 until list.size) {
                        if (list[i].uid == uid) {
                            list.removeAt(i)
                            data.removeAt(i)
                            userAdapter.notifyDataSetChanged()
                            break
                        }
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun getData() {
        var max = 9999999L
        var count = 0
        databaseReference.child("user").addChildEventListener(object : ChildEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                count++
                if (max > snapshot.childrenCount) {
                    max = snapshot.childrenCount
                }
                val userMore = snapshot.getValue(User::class.java)
                if (userMore != null) {
                    if (userMore.uid != UID) {
                        list.add(userMore)
                        data.add(userMore)
                        userAdapter.notifyItemInserted(list.size - 1)
                        if (max == count.toLong()) {
                            getFriends()
                        }
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    if (user.uid == UID) {
                        return
                    }
                    for (i in 0 until list.size) {
                        if (user.uid == list[i].uid) {
                            list[i] = user
                            data[i] = user
                            userAdapter.notifyItemChanged(i)
                            break
                        }
                    }
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    for (i in 0 until list.size) {
                        if (user.uid == list[i].uid) {
                            list.removeAt(i)
                            data.removeAt(i)
                            userAdapter.notifyItemRemoved(i)
                            break
                        }
                    }
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {
                showMessage(error.message, requireContext(), false, null)
            }

        })
    }


    private fun searchByQuery(query: String) {
        list.clear()
        if (query.isNotEmpty()) {
            val result = data.filter { user ->
                user.uid == query || user.fullname!!.lowercase().contains(query.lowercase())
            }.toMutableList() as ArrayList<User>
            list.addAll(result)
        } else {
            list.addAll(data)
        }
        userAdapter.notifyDataSetChanged()
    }

}

