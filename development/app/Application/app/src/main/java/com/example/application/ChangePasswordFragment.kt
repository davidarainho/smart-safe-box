package com.example.application

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.application.data.UserDBSingleton
import com.example.application.data.user.UserDao
import com.example.application.databinding.FragmentChangePasswordBinding
import com.example.application.databinding.FragmentProfilePageBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ChangePasswordFragment : Fragment() {
    private var _binding: FragmentChangePasswordBinding? = null

    private val binding get() = _binding!!
    private lateinit var username : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            username = it.getString("username").toString()
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userDatabase = UserDBSingleton.getInstance(requireContext())
        val userDao: UserDao = userDatabase!!.getAppDatabase().userDao()
        var userId: Int = 0

        binding.changepasswordbutton.setOnClickListener {
            val oldPassword = binding.oldPasswordText.text.toString()
            val newPassword = binding.newPasswordText.text.toString()
            val newPasswordConfirmed = binding.confirmedNewPasswordText.text.toString()

            viewLifecycleOwner.lifecycleScope.launch {
                userId=userDao.getUserIdByUsername(username)
                val userPassword = userDao.getPasswordByUserID(userId)

                if (oldPassword.isEmpty() ||
                    newPassword.isEmpty() ||
                    newPasswordConfirmed.isEmpty()
                ) {
                    Toast.makeText(context, "Error: Fill all entries", Toast.LENGTH_SHORT).show()
                } else if (newPassword != newPasswordConfirmed) {
                    Toast.makeText(
                        context,
                        "Error: Password and Confirmation don't match",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (oldPassword != userPassword) {
                    Toast.makeText(context, "Error: Old password is not correct", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    userDao.updatePassword(userId, newPassword)
                    Toast.makeText(context, "SUCCESS: Your password was updated", Toast.LENGTH_SHORT)
                        .show()

                    binding.oldPasswordText.text?.clear()
                    binding.newPasswordText.text?.clear()
                    binding.confirmedNewPasswordText.text?.clear()
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}