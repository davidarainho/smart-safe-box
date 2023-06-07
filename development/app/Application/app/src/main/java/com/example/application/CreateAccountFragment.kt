package com.example.application

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.application.data.user.User
import com.example.application.data.user.UserDatabase
import com.example.application.data.user.UserDao
import com.example.application.databinding.FragmentCreateAccountBinding
import com.example.application.databinding.FragmentStartBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class CreateAccountFragment : Fragment() {
    private var _binding : FragmentCreateAccountBinding? = null

    private val binding get() = _binding!!

    private lateinit var userDao: UserDao


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCreateAccountBinding.inflate(inflater,container,false)
        return binding.root
    }

    // old version

 /*   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.hasAccount.setOnClickListener {

            findNavController().navigate(R.id.action_createAccountFragment_to_startFragment)
        }
    }*/


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userDao = UserDatabase.getDatabase(requireContext()).userDao()

        binding.signUp.setOnClickListener {
            val username = binding.usernameText.text.toString()
            val email = binding.emailText.text.toString()
            val password = binding.passwordText.text.toString()
            val allowNotifications = 1
            val userID= 45

            val user = User(username, email, password, allowNotifications, userID)

            GlobalScope.launch {
                userDao.upsertUser(user)
            }

            findNavController().navigate(R.id.action_createAccountFragment_to_startFragment)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}