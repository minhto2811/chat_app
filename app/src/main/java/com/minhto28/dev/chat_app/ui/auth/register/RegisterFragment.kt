package com.minhto28.dev.chat_app.ui.auth.register

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.minhto28.dev.chat_app.databinding.FragmentRegisterBinding
import com.minhto28.dev.chat_app.ui.main.MainActivity
import com.minhto28.dev.chat_app.utils.showMessage

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private var uri: Uri? = null

    private var fullname = ""
    private var username = ""
    private var password = ""
    private var repeat_password = ""
    private val registerViewModel: RegisterViewModel by viewModels()


    private val pickImageActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                uri = data?.data
                uri?.let { uri ->
                    val inputStream = requireActivity().contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream?.close()
                    binding.imvAvatar.setImageBitmap(bitmap)
                    binding.btnSignup.enable()
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectImage()
        backToLogin()
        checkField()
        register()
        registerViewModel.success.observe(viewLifecycleOwner) {
            loading(View.GONE, View.VISIBLE)
            when (it) {
                true -> switchScreen()
                false -> showMessage(
                    "The account already exists on the system", requireContext(),
                    false
                ) {
                    binding.tilUsername.error = "Account already exists"
                }

                else -> showMessage(
                    "Error! An error occurred. Please try again later",
                    requireContext(),
                    false,
                    null
                )
            }
        }
    }

    private fun switchScreen() {
        val intent = Intent(requireActivity(), MainActivity::class.java)
        requireActivity().startActivity(intent)
        requireActivity().finish()
    }


    private fun selectImage() {
        binding.selectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImageActivityResult.launch(intent)
        }
    }

    private fun register() {
        binding.btnSignup.setOnClickListener {
            loading(View.VISIBLE, View.GONE)
            registerViewModel.register(uri!!, fullname, username, password)
        }
    }




    private fun checkField() {
        onTextChange(binding.edtFullname, 1)
        onTextChange(binding.edtUsername, 2)
        onTextChange(binding.edtPassword, 3)
        onTextChange(binding.edtRepeatPassword, 4)
    }

    private fun onTextChange(editText: EditText, index: Int) {
        editText.addTextChangedListener {
            when (index) {
                1 -> {
                    fullname = it.toString().trim()
                }

                2 -> {
                    username = it.toString().trim()
                    binding.tilUsername.error =
                        if (username.length < 5) "Username more than 4 characters" else null
                }

                3 -> {
                    password = it.toString().trim()
                    binding.tilPassword.error =
                        if (password.length < 5) "Password more than 4 characters" else null
                }

                4 -> {
                    repeat_password = it.toString().trim()
                    binding.tilRepeatPassword.error =
                        if (repeat_password.length < 5) "Password more than 4 characters" else if (repeat_password != password) "Password does not match" else null
                }

                else -> Log.e("onTextChange: ", "null")
            }
            binding.btnSignup.enable()
        }
    }

    private fun Button.enable() {
        isEnabled =
            username.length > 4 && fullname.isNotEmpty() && password.length > 4 && repeat_password.length > 4 && uri != null && password == repeat_password
    }

    private fun backToLogin() {
        binding.tvLogin.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    private fun loading(progressbar: Int, button: Int) {
        binding.progressCircular.visibility = progressbar
        binding.btnSignup.visibility = button
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}