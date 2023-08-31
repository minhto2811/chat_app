package com.minhto28.dev.chat_app.ui.edit.information

import android.os.Bundle
import android.text.Editable.Factory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.minhto28.dev.chat_app.databinding.FragmentInformationBinding
import com.minhto28.dev.chat_app.ui.main.USER


class InformationFragment : Fragment() {

    private var _binding: FragmentInformationBinding? = null
    private val binding get() = _binding!!
    private val informationViewModel: InformationViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInformationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        USER.observe(viewLifecycleOwner) {
            binding.edtFullname.text = Factory.getInstance().newEditable(it.fullname)
        }
        binding.edtFullname.addTextChangedListener {
            binding.tilFullname.error = if (binding.edtFullname.text.toString().trim()
                    .isEmpty()
            ) "The input field cannot be left blank" else null
            binding.btnSave.enable()
        }

        binding.imvBack.setOnClickListener {
            requireActivity().finish()
        }
        binding.btnSave.setOnClickListener {
            val string =
                binding.edtFullname.text.toString().trim().replace("\\s+".toRegex(), " ")
            binding.edtFullname.setText(string)
            val fullname = binding.edtFullname.text.toString().trim()
            binding.lavLoading.visibility = View.VISIBLE
            binding.btnSave.visibility = View.GONE
            informationViewModel.update(fullname) {
                binding.lavLoading.visibility = View.GONE
                binding.btnSave.visibility = View.VISIBLE
                if (it) {
                    Toast.makeText(requireContext(), "Update successful", Toast.LENGTH_SHORT).show()
                    requireActivity().finish()
                } else {
                    Toast.makeText(requireContext(), "Update failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun Button.enable() {
        isEnabled = binding.edtFullname.text.toString().trim().isNotEmpty()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}