package com.minhto28.dev.chat_app.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.minhto28.dev.chat_app.adapters.UserAdapter
import com.minhto28.dev.chat_app.databinding.FragmentHomeBinding
import com.minhto28.dev.chat_app.models.User
import com.minhto28.dev.chat_app.utils.DataManager


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var userAdapter: UserAdapter
    private lateinit var UID: String
    private lateinit var homeViewModel: HomeViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
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
                val query = newText.toString().trim()
                homeViewModel.filter(query)
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
            binding.progressLoading.visibility = View.GONE
            userAdapter.setData(ArrayList(it.values))
        }
        homeViewModel.dataLiveDataFilter.observe(viewLifecycleOwner) {
            userAdapter.setData(ArrayList(it.values))
        }
    }


}

