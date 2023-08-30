package com.minhto28.dev.chat_app.ui.main.settings

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.minhto28.dev.chat_app.databinding.ChangeAvatarLayoutBinding
import com.minhto28.dev.chat_app.databinding.FragmentSettingsBinding
import com.minhto28.dev.chat_app.models.User
import com.minhto28.dev.chat_app.ui.auth.AuthActivity
import com.minhto28.dev.chat_app.ui.edit.EditActivity
import com.minhto28.dev.chat_app.ui.main.USER
import com.minhto28.dev.chat_app.utils.SharedPrefs
import com.minhto28.dev.chat_app.utils.changeActivity
import com.minhto28.dev.chat_app.utils.clickEffect
import com.minhto28.dev.chat_app.utils.showMessage

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private var user: User? = null
    private val settingsViewModel: SettingsViewModel by viewModels()

    private val pickImageActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val uri = data?.data
                uri?.let { u ->
                    confirmChangeAvatar(u)
                }
            }
        }

    private fun confirmChangeAvatar(uri: Uri) {
        var dialog: AlertDialog? = null
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()
        val alert = AlertDialog.Builder(activity)
        val dialogBinding = ChangeAvatarLayoutBinding.inflate(layoutInflater)
        with(alert) {
            setView(dialogBinding.root)
            setCancelable(false)
        }

        Glide.with(requireContext()).load(user!!.avatar).into(dialogBinding.imvAvatarOld)
        dialogBinding.imvAvatarNew.setImageBitmap(bitmap)
        dialogBinding.btnCancel.setOnClickListener {
            dialogBinding.btnCancel.clickEffect()
            dialog?.cancel()
        }
        dialogBinding.btnConfirm.setOnClickListener {
            dialogBinding.btnConfirm.clickEffect()
            dialog?.cancel()
            settingsViewModel.changeAvatar(uri) {
                val rs = if (it) "Update successful" else "Update failed"
                Toast.makeText(
                    requireContext(), rs, Toast.LENGTH_SHORT
                ).show()
            }
        }
        dialog = alert.create()
        dialog.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUser()
        logout()
        editProfile()
        changePassword()
        changeAvatar()
    }

    private fun changeAvatar() {
        binding.imvAvatar.setOnClickListener {
            binding.imvAvatar.clickEffect()
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImageActivityResult.launch(intent)
        }
    }

    private fun changePassword() {
        binding.lnChangePassword.setOnClickListener {
            binding.lnChangePassword.clickEffect()
            changeActivity(requireActivity(), EditActivity::class.java, false, 2)
        }
    }

    private fun editProfile() {
        binding.lnEditProfile.setOnClickListener {
            binding.lnEditProfile.clickEffect()
            changeActivity(requireActivity(), EditActivity::class.java, false, 1)
        }
    }

    private fun logout() {
        binding.btnLogout.setOnClickListener {
            showMessage("Confirm current account logout", requireContext(), true) {
                SharedPrefs.instance.clear()
                requireActivity().startActivity(Intent(requireContext(), AuthActivity::class.java))
                requireActivity().finish()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUser() {
        USER.observe(viewLifecycleOwner) {
            user = it
            Glide.with(requireActivity()).load(it!!.avatar).into(binding.imvAvatar)
            binding.tvFullname.text = it.fullname
            binding.tvUid.text = "UID: ${it.uid}"
        }
    }
}
