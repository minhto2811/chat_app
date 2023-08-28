package com.minhto28.dev.chat_app.ui.main.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.minhto28.dev.chat_app.R
import com.minhto28.dev.chat_app.adapters.InvitationAdapter
import com.minhto28.dev.chat_app.adapters.UserAdapter
import com.minhto28.dev.chat_app.databinding.FragmentFriendsBinding
import com.minhto28.dev.chat_app.utils.DataManager

class FriendsFragment : Fragment() {
    private var _binding: FragmentFriendsBinding? = null
    private val binding get() = _binding!!
    private var UID: String? = DataManager.getInstance().getUser()?.uid
    private lateinit var userAdapter: UserAdapter
    private var expand = true
    private var height = 0
    private val friendViewModel: FriendViewModel by viewModels()
    private lateinit var invitationAdapter: InvitationAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        expandOrCollapse()
        searchFriend()
        friendViewModel.dataLiveData.observe(viewLifecycleOwner) {
            userAdapter.setData(ArrayList(it.values))
        }
        friendViewModel.dataLiveDataFilter.observe(viewLifecycleOwner) {
            userAdapter.setData(ArrayList(it.values))
        }
        friendViewModel.dataLiveDataInvitation.observe(viewLifecycleOwner) {
            invitationAdapter.setData(ArrayList(it.values))
            binding.tvCountInvitaion.text = "You have received ${it.size} friend requests"
            binding.lnInvitation.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    private fun searchFriend() {
        binding.searchFriend.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val query = newText.toString().trim()
                friendViewModel.filter(query)
                return true

            }

        })
    }

    private fun searchFriendByQuery(query: String) {

        userAdapter.notifyDataSetChanged()
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
        invitationAdapter = InvitationAdapter(UID!!)
        binding.rcvInvitation.adapter = invitationAdapter
        userAdapter = UserAdapter(UID!!, true)
        binding.rcvFriends.adapter = userAdapter
        val divider = DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
        binding.rcvInvitation.addItemDecoration(divider)
        binding.rcvFriends.addItemDecoration(divider)
        binding.rcvInvitation.post {
            height = binding.rcvInvitation.height
        }
    }



}