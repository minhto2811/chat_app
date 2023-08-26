package com.minhto28.dev.chat_app.ui.auth.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.minhto28.dev.chat_app.R
import com.minhto28.dev.chat_app.databinding.FragmentLoginBinding
import com.minhto28.dev.chat_app.ui.main.MainActivity
import com.minhto28.dev.chat_app.utils.showMessage

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by viewModels()


    private var username = ""
    private var password = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        checkField()
        login()
        loginViewModel.success.observe(viewLifecycleOwner) {
            loading(View.GONE, View.VISIBLE)
            when (it) {
                true -> switchScreen()
                false -> showMessage("Incorrect account or password", requireContext(), false, null)
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
        val intent = Intent(requireContext(), MainActivity::class.java)
        requireActivity().startActivity(intent)
        requireActivity().finish()
    }

    private fun login() {
        binding.btnSignin.setOnClickListener {
            loading(View.VISIBLE, View.GONE)
            loginViewModel.login(username, password)
        }
    }

    private fun checkField() {
        onTextChange(binding.edtUsername, 1)
        onTextChange(binding.edtPassword, 2)
    }

    private fun onTextChange(editText: EditText, index: Int) {
        editText.addTextChangedListener {
            when (index) {
                1 -> username = it.toString().trim()
                2 -> password = it.toString().trim()
                else -> Log.e("onTextChange: ", "null")
            }
            binding.btnSignin.enable()
        }
    }


    private fun loading(progressbar: Int, button: Int) {
        binding.progressCircular.visibility = progressbar
        binding.btnSignin.visibility = button
    }

    private fun Button.enable() {
        isEnabled = username.isNotEmpty() && password.isNotEmpty()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}