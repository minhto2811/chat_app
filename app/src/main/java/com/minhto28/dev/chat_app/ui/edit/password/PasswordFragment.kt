package com.minhto28.dev.chat_app.ui.edit.password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.textfield.TextInputLayout
import com.minhto28.dev.chat_app.databinding.FragmentPasswordBinding
import com.minhto28.dev.chat_app.utils.showMessage


class PasswordFragment : Fragment() {

    private var _binding: FragmentPasswordBinding? = null
    private val binding get() = _binding!!
    private val passwordViewModel: PasswordViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imvBack.setOnClickListener {
            requireActivity().finish()
        }
        listtenOnTextChange(binding.edtPasswordOld, binding.tilPasswordOld)
        listtenOnTextChange(binding.edtPasswordNew, binding.tilPasswordNew)
        listtenOnTextChange(binding.edtPasswordNewRepeat, binding.tilPasswordNewRepeat)
        binding.btnSecurity.setOnClickListener {
            visible(View.VISIBLE, View.GONE)
            passwordViewModel.update(binding.edtPasswordOld.get(), binding.edtPasswordNew.get()) {
                visible(View.GONE, View.VISIBLE)
                when (it) {
                    true -> {
                        Toast.makeText(
                            requireContext(), "Update successful", Toast.LENGTH_SHORT
                        ).show()
                        requireActivity().finish()
                    }

                    false -> Toast.makeText(
                        requireContext(), "Password incorect", Toast.LENGTH_SHORT
                    ).show()

                    else -> showMessage(
                        "Error! An error occurred. Please try again later", requireContext(), false
                    ) {
                        requireActivity().finish()
                    }
                }
            }
        }
    }


    private fun visible(progress: Int, button: Int) {
        binding.btnSecurity.visibility = button
        binding.progressCircular.visibility = progress
    }


    private fun listtenOnTextChange(editText: EditText, textInputLayout: TextInputLayout) {
        editText.addTextChangedListener {
            if (it.toString().contains("[^A-Za-z0-9]+".toRegex())) {
                val string = it.toString().trim().replace("[^A-Za-z0-9]+".toRegex(), "")
                editText.setText(string)
                editText.setSelection(string.length)
            }
            if (editText.text.toString().trim().length < 5) {
                textInputLayout.error = "The input field requires at least 5 characters"
            } else {
                textInputLayout.error = null
            }
            if (editText == binding.edtPasswordNewRepeat) {
                if (binding.edtPasswordNew.get().length > 4) {
                    if (editText.get() != binding.edtPasswordNew.get()) {
                        textInputLayout.error = "Passwords do not match"
                    } else {
                        textInputLayout.error = null
                    }
                }
            } else if (editText == binding.edtPasswordNew) {
                if (binding.edtPasswordNewRepeat.get().length > 4) {
                    if (editText.get() != binding.edtPasswordNewRepeat.get()) {
                        binding.tilPasswordNewRepeat.error = "Passwords do not match"
                    } else {
                        binding.tilPasswordNewRepeat.error = null
                    }
                }
            }
            binding.btnSecurity.enable()
        }
    }


    private fun Button.enable() {
        isEnabled =
            binding.edtPasswordOld.get().length > 4 && binding.edtPasswordNew.get().length > 4 && binding.edtPasswordNew.get() == binding.edtPasswordNewRepeat.get()
    }

    private fun EditText.get(): String {
        return text.toString().trim()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}