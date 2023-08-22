package com.minhto28.dev.chat_app.ui.main

import SharedPrefs
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.minhto28.dev.chat_app.databinding.FragmentSettingsBinding
import com.minhto28.dev.chat_app.models.User
import com.minhto28.dev.chat_app.ui.auth.AuthActivity
import com.minhto28.dev.chat_app.utils.DataManager
import com.minhto28.dev.chat_app.utils.showMessage

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private var user: User? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        user = DataManager.getInstance().getUser()
        Log.e("user", "${user.toString()}")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUser()
        logout()
    }

    private fun logout() {
        binding.btnLogout.setOnClickListener {
            showMessage("Confirm current account logout", requireContext(), true) {
                user!!.setStatusOnline(false)
                SharedPrefs.instance.clear()
                requireActivity().startActivity(Intent(requireContext(), AuthActivity::class.java))
                requireActivity().finish()
            }
        }
    }

    private fun setUser() {
        if (user != null) {
            Glide.with(requireActivity()).load(user!!.avatar).into(binding.imvAvatar)
            binding.tvFullname.text = user!!.fullname
            binding.tvUid.text = "UID: ${user!!.uid}"
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}