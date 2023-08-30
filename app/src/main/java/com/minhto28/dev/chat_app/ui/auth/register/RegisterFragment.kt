package com.minhto28.dev.chat_app.ui.auth.register

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
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
import com.google.android.material.textfield.TextInputLayout
import com.minhto28.dev.chat_app.databinding.FragmentRegisterBinding
import com.minhto28.dev.chat_app.ui.main.MainActivity
import com.minhto28.dev.chat_app.utils.changeActivity
import com.minhto28.dev.chat_app.utils.showMessage

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private var uri: Uri? = null

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
            registerViewModel.register(
                uri!!,
                binding.edtFullname.text.toString().trim(),
                binding.edtUsername.text.toString().trim(),
                binding.edtPassword.text.toString().trim(),
            ){
                loading(View.GONE, View.VISIBLE)
                when (it) {
                    true -> changeActivity(requireActivity(), MainActivity::class.java)
                    false -> showMessage(
                        "The account already exists on the system", requireContext(), false
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
    }




    private fun checkField() {
        binding.edtFullname.onFocusChangeListener = View.OnFocusChangeListener { _  , hasFocus ->
            if (!hasFocus) {
                val string =
                    binding.edtFullname.text.toString().trim().replace("\\s+".toRegex(), " ")
                binding.edtFullname.setText(string)
            }
        }
        onTextChange(binding.edtFullname, binding.tilFullname, true)
        onTextChange(binding.edtUsername, binding.tilUsername, false)
        onTextChange(binding.edtPassword, binding.tilPassword, false)
        onTextChange(binding.edtRepeatPassword, binding.tilRepeatPassword, false)
    }

    private fun onTextChange(editText: EditText, textInputLayout: TextInputLayout, space: Boolean) {
        editText.addTextChangedListener {
            val rx = if (space) "[^A-Za-z0-9 ]+" else "[^A-Za-z0-9]+"
            if (it.toString().contains(rx.toRegex())) {
                val string =
                    it.toString().trim().replace(rx.toRegex(), "")
                editText.setText(string)
                editText.setSelection(string.length)
            }
            if (space) {
                textInputLayout.error = if (editText.text.toString().trim()
                        .isEmpty()
                ) "The input field cannot be left blank" else null
            } else {
                textInputLayout.error = if (editText.text.toString()
                        .trim().length < 5
                ) "The input field requires at least 5 characters" else null
                if (editText == binding.edtPassword || editText == binding.edtRepeatPassword && (binding.edtPassword.text.toString()
                        .trim().length > 4 && binding.edtRepeatPassword.text.toString()
                        .trim().length > 4)
                ) {
                    binding.edtRepeatPassword.error = if (binding.edtPassword.text.toString()
                            .trim() != binding.edtRepeatPassword.text.toString().trim()
                    ) "Passwords do not match" else null
                }
            }
            binding.btnSignup.enable()
        }
    }

    private fun Button.enable() {
        isEnabled = binding.edtFullname.text.toString().trim()
            .isNotEmpty() && binding.edtUsername.text.toString()
            .trim().length > 4 && binding.edtPassword.text.toString()
            .trim().length > 4 && uri != null && binding.edtRepeatPassword.text.toString()
            .trim().length > 4 && binding.edtPassword.text.toString()
            .trim() == binding.edtRepeatPassword.text.toString().trim()
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