package com.minhto28.dev.chat_app.ui.auth

import SharedPrefs
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
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.minhto28.dev.chat_app.R
import com.minhto28.dev.chat_app.databinding.FragmentLoginBinding
import com.minhto28.dev.chat_app.models.Account
import com.minhto28.dev.chat_app.models.User
import com.minhto28.dev.chat_app.ui.main.MainActivity
import com.minhto28.dev.chat_app.utils.DataManager
import com.minhto28.dev.chat_app.utils.showMessage

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private var username = ""
    private var password = ""
    private var account: Account? = null
    private var user: User? = null
    private lateinit var databaseRefeernce: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        databaseRefeernce = Firebase.database.reference
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

    }

    private fun login() {
        binding.btnSignin.setOnClickListener {
            loading(View.VISIBLE, View.GONE)
            databaseRefeernce.child("account").child(username)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        account = snapshot.getValue(Account::class.java)
                        if (account != null) {
                            databaseRefeernce.child("user").child(account!!.uid)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        user = snapshot.getValue(User::class.java)
                                        if (user != null) {
                                            user!!.setStatusOnline(true)
                                            SharedPrefs.instance.put(account)
                                            DataManager.getInstance().setAccount(account!!)
                                            DataManager.getInstance().setUser(user!!)
                                            loading(View.GONE, View.VISIBLE)
                                            val intent =
                                                Intent(requireContext(), MainActivity::class.java)
                                            requireActivity().startActivity(intent)
                                            requireActivity().finish()
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        loading(View.GONE, View.VISIBLE)
                                        showMessage(error.message, requireContext(), false,null)
                                    }

                                })
                        } else {
                            loading(View.GONE, View.VISIBLE)
                            showMessage("Incorrect account or password", requireContext(), false,null)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        loading(View.GONE, View.VISIBLE)
                        showMessage(error.message, requireContext(), false,null)
                    }

                })
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