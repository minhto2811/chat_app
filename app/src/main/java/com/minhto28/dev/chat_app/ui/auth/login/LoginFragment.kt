package com.minhto28.dev.chat_app.ui.auth.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.minhto28.dev.chat_app.R
import com.minhto28.dev.chat_app.databinding.FragmentLoginBinding
import com.minhto28.dev.chat_app.ui.main.MainActivity
import com.minhto28.dev.chat_app.utils.changeActivity
import com.minhto28.dev.chat_app.utils.showMessage

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by viewModels()


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
                true -> changeActivity(requireActivity(), MainActivity::class.java)
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


    private fun login() {
        binding.btnSignin.setOnClickListener {
            loading(View.VISIBLE, View.GONE)
            loginViewModel.login(
                binding.edtUsername.text.toString().trim(),
                binding.edtPassword.text.toString().trim()
            )
        }
    }

    private fun checkField() {
        onTextChange(binding.edtUsername, binding.tilUsername)
        onTextChange(binding.edtPassword, binding.tilPassword)
    }

    private fun onTextChange(editText: EditText, textInputLayout: TextInputLayout) {
        editText.addTextChangedListener {
            if (it.toString().contains("[^A-Za-z0-9]+".toRegex())) {
                val string = it.toString().trim().replace("[^A-Za-z0-9]+".toRegex(), "")
                editText.setText(string)
                editText.setSelection(string.length)
                textInputLayout.error =
                    if (string.length < 5) "The input field requires at least 5 characters" else null
            }
            binding.btnSignin.enable()
        }
    }


    private fun loading(progressbar: Int, button: Int) {
        binding.progressCircular.visibility = progressbar
        binding.btnSignin.visibility = button
    }

    private fun Button.enable() {
        isEnabled = binding.edtUsername.text.toString()
            .trim().length > 4 && binding.edtPassword.text.toString().trim().length > 4
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}