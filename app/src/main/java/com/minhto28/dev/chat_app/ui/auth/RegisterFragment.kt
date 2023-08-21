package com.minhto28.dev.chat_app.ui.auth

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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.minhto28.dev.chat_app.databinding.FragmentRegisterBinding
import com.minhto28.dev.chat_app.models.Account
import com.minhto28.dev.chat_app.models.User
import com.minhto28.dev.chat_app.ui.main.MainActivity
import com.minhto28.dev.chat_app.utils.SharedPrefs
import com.minhto28.dev.chat_app.utils.generateUniqueID
import com.minhto28.dev.chat_app.utils.showMessage

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private var uri: Uri? = null

    private var fullname = ""
    private var username = ""
    private var password = ""
    private var repeat_password = ""
    private lateinit var database: DatabaseReference

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
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = Firebase.database.reference
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
            database.child("account").child(username)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.value == null) {
                            saveImage()
                        } else {
                            loading(View.GONE, View.VISIBLE)
                            showMessage("This account has already existed!", requireContext(), null)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        loading(View.GONE, View.VISIBLE)
                        showMessage(error.message!!, requireContext(), null)
                        Log.e("check", error.message)
                    }

                })

        }
    }

    private fun saveImage() {
        Log.e("saveImage uri", uri.toString())
        val uid = generateUniqueID()
        val ref = FirebaseStorage.getInstance().reference.child("avatar/uid_${uid}.jpg")
        val uploadTask = ref.putFile(uri!!)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    Log.e("saveImage exception", it.toString())
                    throw it
                }
            }
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                createUser(downloadUri, uid)
            } else {
                loading(View.GONE, View.VISIBLE)
                showMessage(
                    "Error! An error occurred. Please try again later",
                    requireContext(),
                    null
                )
                Log.e("saveImage", "Lỗi khi lưu ảnh kiểm tra lại quyền đọc ghi storage")
            }
        }
    }

    private fun createUser(downloadUri: Uri, uid: String) {
        val account = Account(uid, username, password)
        val user = User(uid, downloadUri.toString(), fullname, true)

        val accountRef = database.child("account").child(username)
        val userRef = database.child("user").child(uid)
        accountRef.setValue(account).addOnSuccessListener {
            userRef.setValue(user).addOnSuccessListener {
                loading(View.GONE, View.VISIBLE)
                SharedPrefs.instance.put(SharedPrefs.ACCOUNT, account)
                SharedPrefs.instance.put(SharedPrefs.USER, user)
                Toast.makeText(requireContext(), "Account successfully created", Toast.LENGTH_SHORT)
                    .show()
                val intent = Intent(requireActivity(), MainActivity::class.java)
                intent.putExtra("account", account)
                intent.putExtra("user", user)
                requireActivity().startActivity(intent)
                requireActivity().finish()
            }.addOnFailureListener {
                loading(View.GONE, View.VISIBLE)
                showMessage(it.message!!, requireContext(), null)
                Log.e("user ref: ", it.toString())
            }
        }.addOnFailureListener {
            loading(View.GONE, View.VISIBLE)
            showMessage(it.message!!, requireContext(), null)
            Log.e("account ref: ", it.toString())
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
                1 -> fullname = it.toString().trim()
                2 -> username = it.toString().trim()
                3 -> password = it.toString().trim()
                4 -> repeat_password = it.toString().trim()
                else -> Log.e("onTextChange: ", "null")
            }
            binding.btnSignup.enable()
        }
    }

    private fun Button.enable() {
        isEnabled =
            username.isNotEmpty() && fullname.isNotEmpty() && password.isNotEmpty() && repeat_password.isNotEmpty() && uri != null && password.equals(
                repeat_password
            )
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