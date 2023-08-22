package com.minhto28.dev.chat_app.ui.auth

import SharedPrefs
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.minhto28.dev.chat_app.R
import com.minhto28.dev.chat_app.databinding.FragmentSplashBinding
import com.minhto28.dev.chat_app.models.Account
import com.minhto28.dev.chat_app.models.User
import com.minhto28.dev.chat_app.ui.main.MainActivity
import com.minhto28.dev.chat_app.utils.DataManager

class SplashFragment : Fragment() {
    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    private var timeStart = 0L
    private var timeEnd = 0L

    private var user: User? = null
    private var account: Account? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        database = Firebase.database.reference
        timeStart = System.currentTimeMillis()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkAccount()
    }

    private fun checkAccount() {
        account = SharedPrefs.instance.get(SharedPrefs.ACCOUNT)
        if (account != null) {
            val accountRef =
                database.child("account").child(account!!.username)
            accountRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val account = snapshot.getValue(Account::class.java)
                    if (account != null) {
                        getUser(account)
                    } else {
                        navigation(0)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    navigation(0)
                }

            })
        } else {
            navigation(0)
        }
    }

    private fun getUser(account: Account) {
        val userRef = database.child("user").child(account.uid)
        userRef.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    user = snapshot.getValue(User::class.java)
                    if (user != null) {
                        DataManager.getInstance().setAccount(account)
                        DataManager.getInstance().setUser(user!!)
                        navigation(1)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    navigation(0)
                }

            }
        )
    }

    private fun navigation(status: Int) {
        timeEnd = System.currentTimeMillis()
        val timeRun = timeEnd - timeStart
        if (timeRun < 4000) {
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    selectFunc(status)
                }, 4000 - timeRun
            )
        } else {
            selectFunc(status)
        }
    }

    private fun selectFunc(status: Int) {
        if (status == 0) {
            findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
        } else {
            val intent = Intent(requireContext(), MainActivity::class.java)
            requireActivity().startActivity(intent)
            requireActivity().finish()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}