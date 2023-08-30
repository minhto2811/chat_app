package com.minhto28.dev.chat_app.ui.auth.splash

import com.minhto28.dev.chat_app.utils.SharedPrefs
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.minhto28.dev.chat_app.R
import com.minhto28.dev.chat_app.databinding.FragmentSplashBinding
import com.minhto28.dev.chat_app.models.Account
import com.minhto28.dev.chat_app.ui.main.MainActivity
import com.minhto28.dev.chat_app.utils.changeActivity

class SplashFragment : Fragment() {
    private var _binding: FragmentSplashBinding? = null
    private val splashViewModel: SplashViewModel by viewModels()
    private val binding get() = _binding!!
    private var timeStart = 0L
    private var timeEnd = 0L

    private var account: Account? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        timeStart = System.currentTimeMillis()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkAccount()
        splashViewModel.success.observe(viewLifecycleOwner) {
            timeEnd = System.currentTimeMillis()
            when (it) {
                true -> navigation(1)
                false -> navigation(0)
            }
        }
    }

    private fun checkAccount() {
        account = SharedPrefs.instance[SharedPrefs.ACCOUNT]
        if (account != null) {
            splashViewModel.login(account!!)
        } else {
            navigation(0)
        }
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
            changeActivity(requireActivity(),MainActivity::class.java)
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}