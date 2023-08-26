package com.minhto28.dev.chat_app.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.minhto28.dev.chat_app.adapters.UserAdapter
import com.minhto28.dev.chat_app.databinding.FragmentHomeBinding
import com.minhto28.dev.chat_app.utils.DataManager


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userAdapter: UserAdapter
    private lateinit var UID: String
    private lateinit var homeViewModel: HomeViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        databaseReference = Firebase.database.reference
        UID = DataManager.getInstance().getUser()!!.uid!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inintView()
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
        homeViewModel = HomeViewModel()
        userAdapter = UserAdapter(UID)
        binding.rcvUser.adapter = userAdapter
        val divider = DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
        binding.rcvUser.addItemDecoration(divider)
        homeViewModel.dataLiveData.observe(viewLifecycleOwner) {
            userAdapter.setData(it!!)
        }
    }



    private fun searchByQuery(query: String) {
        userAdapter.notifyDataSetChanged()
    }

}

